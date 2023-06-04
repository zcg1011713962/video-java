package org.video.rtsp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
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
public class RtspResponseHandler extends SimpleChannelInboundHandler<DefaultHttpResponse> implements ResponseHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("已连接");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接被关闭");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DefaultHttpResponse msg) throws Exception {
        Client client = ClientManager.client(ctx.channel().id().asLongText());
        log.info("---------------------------------------");
        int code = msg.status().code();
        HttpHeaders headers = msg.headers();
        String CSeq = headers.get("CSeq");
        int cseq = Integer.parseInt(CSeq) + 1;
        if (code == HttpResponseStatus.OK.code()) {
            log.info("{}", msg);
            success(client, cseq);
        } else {
            if (code == HttpResponseStatus.UNAUTHORIZED.code()) {
                // client.write(RtspReqPacket.describe(url), Method.DESCRIBE);
                // WWW-Authenticate -> Digest realm="IP Camera(J2914)", nonce="446c8be9ca7682b00f43b2bae7e98b86", stale="FALSE"
                String authenticate = headers.get("WWW-Authenticate");
                log.info("{}", authenticate);
                Map<String, String> authMap = decode(authenticate);
                RtspUrlParser rtspParser = new RtspUrlParser(client.url());
                if (rtspParser.parse()) {
                    RTSPDigest digest = new RTSPDigest(rtspParser.getUsername(), authMap.get("realm"), authMap.get("nonce"), rtspParser.getUri(), RtspMethods.DESCRIBE.name(), rtspParser.getPassword());
                    String response = digest.calculateResponse();
                    RtspReqPacket.auth(rtspParser.getUri(), rtspParser.getUsername(), authMap.get("nonce"), authMap.get("realm"), response, cseq);
                    return;
                }
            }
            log.error("{}", msg);
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
