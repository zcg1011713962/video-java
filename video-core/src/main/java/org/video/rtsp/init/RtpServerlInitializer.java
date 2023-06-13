package org.video.rtsp.init;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.proxy.Socks4ProxyHandler;
import org.video.rtsp.rtp.RtpResponseHandler;

import java.net.InetSocketAddress;

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
