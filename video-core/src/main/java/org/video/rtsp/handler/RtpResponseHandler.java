package org.video.rtsp.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.video.manager.ServerManager;
import org.video.netty.abs.AbstractInboundHandler;
import org.video.rtsp.RtpServer;

@Slf4j
public class RtpResponseHandler extends AbstractInboundHandler<DatagramPacket> {
    @Autowired(required = false)
    private ApplicationEventPublisher publisher;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        RtpServer rtpServer  = (RtpServer) ServerManager.server(ctx.channel().id().asLongText());
        log.info("upd数据包 len:{} {}", msg.content().capacity(), msg);
    }


}
