package org.video.rtsp.init;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.video.rtsp.rtp.RtpResponseHandler;

/**
 * 入站事件在ChannelPipeline中由头指针向尾指针传播，只处理Inbound类型的Handler
 * 出站事件由尾指针向头指针传播，只处理Outbound类型的Handler
 */
public class RtpServerlInitializer extends ChannelInitializer<NioDatagramChannel> {
    boolean proxy;

    public RtpServerlInitializer(boolean proxy) {
        this.proxy = proxy;
    }

    protected void initChannel(NioDatagramChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("rtpResponseHandler", new RtpResponseHandler()); // Inbound
    }
}
