package org.video.rtsp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.video.eum.Method;
import org.video.exception.BaseException;
import org.video.netty.Client;
import org.video.netty.ClientManager;
import org.video.netty.ResponseHandler;
import org.video.rtsp.entity.RtspReqPacket;

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
        String url = client.url();
        log.info("---------------------------------------");
        if(msg.status().code() == HttpResponseStatus.OK.code()){
            log.info("url:{} msg:{}", url, msg.headers());
            switch (client.methodACK()){
                case OPTIONS:
                    client.write(RtspReqPacket.describe(url), Method.DESCRIBE);
                    break;
                case FIN:
                    throw new BaseException("消息发送失败");
                default:
                    break;
            }
        }else{
            log.error("url:{} msg:{}", url, msg);
        }
    }


}
