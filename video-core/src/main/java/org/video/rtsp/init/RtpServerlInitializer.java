package org.video.rtsp.init;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.video.rtsp.rtp.RtpResponseHandler;

public class RtpServerlInitializer  extends ChannelInitializer<NioDatagramChannel> {
    protected void initChannel(NioDatagramChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("rtpResponseHandler", new RtpResponseHandler());
    }
}
