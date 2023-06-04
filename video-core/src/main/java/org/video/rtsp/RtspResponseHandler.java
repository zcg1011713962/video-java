package org.video.rtsp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.video.eum.Method;
import org.video.exception.BaseException;
import org.video.netty.Client;
import org.video.netty.ClientManager;
import org.video.netty.ResponseHandler;
import org.video.rtsp.entity.RtspReqPacket;
import org.video.util.RTSPDigest;
import org.video.util.RtspUrlParser;

import java.util.Map;
import java.util.WeakHashMap;

@Slf4j
public class RtspResponseHandler extends SimpleChannelInboundHandler<DefaultHttpObject> implements ResponseHandler {

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
        Client client = ClientManager.client(ctx.channel().id().asLongText());
        log.info("---------------------------------------");
        log.info("{}", msg);
        log.info("---------------------------------------");
        if(msg instanceof DefaultHttpResponse){
            DefaultHttpResponse httpResponse = (DefaultHttpResponse) msg;
            int code = httpResponse.status().code();
            HttpHeaders headers = httpResponse.headers();
            String CSeq = headers.get("CSeq");
            int cseq = Integer.parseInt(CSeq) + 1;
            log.info("{}", msg);
            if (code == HttpResponseStatus.OK.code()) {
                success(client, cseq);
            } else {
                if (code == HttpResponseStatus.UNAUTHORIZED.code()) {
                    // WWW-Authenticate -> Digest realm="IP Camera(J2914)", nonce="446c8be9ca7682b00f43b2bae7e98b86", stale="FALSE"
                    String authenticate = headers.get("WWW-Authenticate");
                    Map<String, String> authMap = decode(authenticate);
                    RtspUrlParser rtspParser = new RtspUrlParser(client.url());
                    if (rtspParser.parse()) {
                        RTSPDigest digest = new RTSPDigest(rtspParser.getUsername(), authMap.get("realm"), authMap.get("nonce"), rtspParser.getUri(), RtspMethods.DESCRIBE.name(), rtspParser.getPassword());
                        String response = digest.calculateResponse();
                        client.write(RtspReqPacket.auth(rtspParser.getUri(), rtspParser.getUsername(), authMap.get("nonce"), authMap.get("realm"), response, cseq), Method.DESCRIBE);
                        return;
                    }
                }
            }
        }else if(msg instanceof HttpContent){
            if(msg instanceof  DefaultHttpContent){
                DefaultHttpContent httpContent = (DefaultHttpContent) msg;
                ByteBuf content = httpContent.content();
            }else if(msg instanceof DefaultLastHttpContent){
                DefaultLastHttpContent httpContent = (DefaultLastHttpContent) msg;
            }
        }
    }


    private void success(Client client, int cseq) {
        switch (client.methodACK()) {
            case OPTIONS:
                client.write(RtspReqPacket.describe(client.uri(), cseq), Method.DESCRIBE);
                break;
            case FIN:
                throw new BaseException("消息发送失败");
            default:
                break;
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
