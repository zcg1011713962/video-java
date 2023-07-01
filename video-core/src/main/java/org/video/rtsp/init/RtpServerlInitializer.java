package org.video.rtsp.init;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.video.netty.abs.AbstractInitializer;
import org.video.rtsp.handler.RtpResponseHandler;

/**
 * 入站事件在ChannelPipeline中由头指针向尾指针传播，只处理Inbound类型的Handler
 * 出站事件由尾指针向头指针传播，只处理Outbound类型的Handler
 */
public class RtpServerlInitializer extends AbstractInitializer<NioDatagramChannel> {

    public RtpServerlInitializer(boolean proxy) {
        super(proxy);
    }

    protected void initChannel(NioDatagramChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("rtpResponseHandler", new RtpResponseHandler()); // Inbound
    }
}
