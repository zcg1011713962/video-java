package org.video.rtsp;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspHeaderValues;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.video.eum.Method;
import org.video.exception.BaseException;
import org.video.netty.ClientManager;
import org.video.netty.ResponseHandler;
import org.video.rtsp.entity.RtspReqPacket;
import org.video.util.RTSPDigest;
import org.video.util.RtspSDParser;
import org.video.util.RtspUrlParser;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RtspResponseHandler extends SimpleChannelInboundHandler<DefaultHttpObject> implements ResponseHandler {
    private AtomicInteger count = new AtomicInteger();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("已连接");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接被关闭");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DefaultHttpObject msg) throws Exception {
        RtspClient rtspClient = (RtspClient) ClientManager.client(ctx.channel().id().asLongText());
        log.info("消息编号{}-------------------", count.incrementAndGet());
        if(msg instanceof DefaultHttpResponse){
            DefaultHttpResponse httpResponse = (DefaultHttpResponse) msg;
            int code = httpResponse.status().code();
            HttpHeaders headers = httpResponse.headers();
            int responseCseq = Integer.parseInt(headers.get(RtspHeaderNames.CSEQ));
            int requestCseq = responseCseq + 1;
            log.info("消息编号{} \n{}", count.get(), msg);
            if (code ==  RtspResponseStatuses.OK.code()) {
                String contentType = headers.get(RtspHeaderNames.CONTENT_TYPE);
                String pb = headers.get(RtspHeaderNames.PUBLIC);
                if(StrUtil.isNotBlank(pb)){
                    rtspClient.write(RtspReqPacket.describe(rtspClient.uri(), requestCseq), Method.DESCRIBE);
                }
                if("application/sdp".equals(contentType)){
                    String contentLength = headers.get(RtspHeaderNames.CONTENT_LENGTH);
                    rtspClient.getRtspSDParser().setContentLength(Integer.parseInt(contentLength));
                }
            } else {
                if (code == RtspResponseStatuses.UNAUTHORIZED.code()) {
                    // WWW-Authenticate -> Digest realm="IP Camera(J2914)", nonce="446c8be9ca7682b00f43b2bae7e98b86", stale="FALSE"
                    String authenticate = headers.get("WWW-Authenticate");
                    Map<String, String> authMap = decode(authenticate);
                    RtspUrlParser rtspParser = new RtspUrlParser(rtspClient.url());
                    if (rtspParser.parse()) {
                        RTSPDigest digest = new RTSPDigest(rtspParser.getUsername(), authMap.get("realm"), authMap.get("nonce"), rtspParser.getUri(), RtspMethods.DESCRIBE.name(), rtspParser.getPassword());
                        String response = digest.calculateResponse();
                        rtspClient.write(RtspReqPacket.auth(rtspParser.getUri(), rtspParser.getUsername(), authMap.get("nonce"), authMap.get("realm"), response, requestCseq), Method.DESCRIBE);
                        return;
                    }
                }
            }
        }else if(msg instanceof HttpContent){
            if(msg instanceof  DefaultHttpContent){
                DefaultHttpContent httpContent = (DefaultHttpContent) msg;
                ByteBuf buf;
                if(httpContent != null && (buf = httpContent.content()) != null){
                    String c = buf.toString(Charset.forName("UTF-8"));
                    log.info("消息编号{} \n{}", count.get(), c);
                    boolean ret = rtspClient.getRtspSDParser().append(c).parser();
                    if(ret) log.info("解析SDP \n{}", rtspClient.getRtspSDParser());
                }
            }else if(msg instanceof DefaultLastHttpContent){
                DefaultLastHttpContent httpContent = (DefaultLastHttpContent) msg;
                ByteBuf buf;
                if(httpContent != null && ( buf = httpContent.content()) != null){
                    String c = buf.toString(Charset.forName("UTF-8"));
                    log.info("消息编号{} \n{}", count.get(), c);
                }
            }
        }
    }




    public static Map<String, String> decode(String authenticate) {
        String[] parts = authenticate.split(String.valueOf(StringUtil.COMMA));
        WeakHashMap<String, String> params = new WeakHashMap<>();
        String[] a = parts[0].split(String.valueOf(StringUtil.SPACE));
        String scheme = a[0];
        params.put("scheme", scheme);
        String realmEntry = parts[0].replace(scheme, "").trim();
        String val = realmEntry.split("=")[1];
        if (realmEntry.startsWith("realm")) {
            params.put("realm", val.replaceAll("\"", ""));
        } else if (realmEntry.startsWith("nonce")) {
            params.put("nonce", val.replaceAll("\"", ""));
        }
        for (int i = 1; i < parts.length; i++) {
            String[] entry = parts[i].split("=");
            if (parts[i].trim().startsWith("realm")) {
                params.put("realm", entry[1].replaceAll("\"", ""));
            } else if (parts[i].trim().startsWith("nonce")) {
                params.put("nonce", entry[1].replaceAll("\"", ""));
            }
        }

        return params;
    }

}
