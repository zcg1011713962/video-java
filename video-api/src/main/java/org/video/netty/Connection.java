package org.video.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.video.entity.BaseEntity;
import org.video.eum.Protocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public interface Connection<T> extends ClientHandle<T>, ServerHandle<T> {
    ConcurrentMap<Protocol, BaseEntity> protocolMap = new ConcurrentHashMap<>();
    // LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
    // client
    Bootstrap bootstrap = new Bootstrap();
    // tcp server
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    // udp server
    NioEventLoopGroup group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 4);

    /**
     * 获取客户端 Bootstrap
     *
     * @return
     */
    default Bootstrap getBootstrap(Client client) {
        ChannelHandler channelHandler = protocolMap.get(client.protocol()).getChannelHandler();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(channelHandler)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_RCVBUF, Integer.MAX_VALUE)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 65536 * 100));
        return bootstrap;
    }

    default ServerBootstrap getTCPServer(Server server) {
        ServerBootstrap serverBootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(protocolMap.get(server.protocol()).getChannelHandler());
        return serverBootstrap;
    }

    default Bootstrap getUDPServer(Server server) {
        ChannelHandler channelHandler = protocolMap.get(server.protocol()).getChannelHandler();
        Bootstrap bootstrap = new Bootstrap().group(group)
                .option(ChannelOption.SO_BROADCAST, true)
                .channel(NioDatagramChannel.class)
                // .handler(loggingHandler)
                .handler(channelHandler);
        return bootstrap;
    }

    T write(ByteBuf byteBuf);

    T close();

}
