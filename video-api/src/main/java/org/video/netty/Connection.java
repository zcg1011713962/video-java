package org.video.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.video.entity.BaseEntity;
import org.video.eum.Protocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public interface Connection<T> extends Handle<T>{
    Bootstrap bootstrap = new Bootstrap();
    ConcurrentMap<Protocol, BaseEntity> protocolMap = new ConcurrentHashMap<>();

    /**
     * 获取客户端 Bootstrap
     * @return
     */
    default Bootstrap getBootstrap(Client client){
        bootstrap.group(new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 4))
                .channel(NioSocketChannel.class)
                .handler(protocolMap.get(client.protocol()).getChannelHandler())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_RCVBUF, Integer.MAX_VALUE)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 65536 * 100));
        return bootstrap;
    }

}
