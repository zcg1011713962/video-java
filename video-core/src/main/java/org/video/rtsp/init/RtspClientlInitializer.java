package org.video.rtsp.init;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.rtsp.RtspDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.video.netty.HeartBeatHandler;
import org.video.rtsp.RtspResponseHandler;

import java.util.concurrent.TimeUnit;

/**
 * Inbound（入站）事件由Inbound处理程序以自下而上的方向处理
 * Outbound（出站）事件由Outbound处理程序在自上而下的方向进行处理
 */
public class RtspClientlInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("rtspIdleStateHandler", new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast("rtspHeartBeatHandler", new HeartBeatHandler());
        pipeline.addLast("rtspDecoder", new RtspDecoder());
        pipeline.addLast("rtspResponseHandler", new RtspResponseHandler());
        // pipeline.addLast("rtspEncoder", new RtspEncoder());
    }

}
