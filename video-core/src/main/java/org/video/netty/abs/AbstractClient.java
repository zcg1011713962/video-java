package org.video.netty.abs;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.video.eum.Protocol;
import org.video.exception.BaseException;
import org.video.manager.CacheManager;
import org.video.manager.EventLoopGroupManager;
import org.video.netty.Client;

import java.util.Optional;

public abstract class AbstractClient<T> implements Client<T> {
    private ChannelHandler channelHandler;
    private static Bootstrap bootstrap;

    @Override
    public EventLoopGroup getWorkerGroup() {
        return EventLoopGroupManager.getWorkGroup();
    }

    protected void channelHandler(Protocol protocol) {
        Optional<ChannelHandler> optional = CacheManager.protocolTable().row(protocol).keySet().stream().findFirst();
        if(optional.isPresent()){
            channelHandler = optional.get();
            return;
        }
        throw new BaseException("获取channelHandler失败");
    }

    @Override
    public Bootstrap getTcpClient() {
        if (bootstrap != null) {
            return bootstrap;
        }
        bootstrap = new Bootstrap().group(getWorkerGroup())
                .channel(NioSocketChannel.class)
                .handler(channelHandler)
                .option(ChannelOption.SO_KEEPALIVE, true) // 长连接
                .option(ChannelOption.SO_RCVBUF, Integer.MAX_VALUE)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 65536 * 100))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15);
        return bootstrap;
    }
}
