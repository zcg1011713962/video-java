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
import org.video.entity.BaseEntity;
import org.video.eum.Protocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public interface Connection<T> extends ClientHandle<T>, ServerHandle<T> {
    ConcurrentMap<Protocol, BaseEntity> protocolMap = new ConcurrentHashMap<>();
    // LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);

    /**
     *
     * bossGroup 用于接受客户端连接
     * @return
     */
    default EventLoopGroup getBossGroup(){
        return new NioEventLoopGroup(1);
    }

    /**
     * 一个NioEventLoopGroup包含一个或多个NioEventLoop，每个NioEventLoop运行在一个独立的线程中，并处理一个或多个Channel的所有事件
     * workerGroup 用于处理已接受连接的流量
     * @return
     */
    default EventLoopGroup getWorkerGroup(){
        return new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
    }

    /**
     *
     * tcp client
     * @return
     */
    default Bootstrap getBootstrap(Client client) {
        ChannelHandler channelHandler = protocolMap.get(client.protocol()).getChannelHandler();
        Bootstrap bootstrap = new Bootstrap().group(getWorkerGroup())
                .channel(NioSocketChannel.class)
                .handler(channelHandler)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_RCVBUF, Integer.MAX_VALUE)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 65536 * 100))
                .option(ChannelOption.SO_TIMEOUT, 15) // 读取数据超时
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15);
        return bootstrap;
    }

    /**
     *  tcp server
     * @param server
     * @return
     */
    default ServerBootstrap getTCPServer(Server server) {
        ServerBootstrap serverBootstrap = new ServerBootstrap().group(getBossGroup(), getWorkerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(protocolMap.get(server.protocol()).getChannelHandler());
        return serverBootstrap;
    }

    /**
     *  udp server
     * @param server
     * @return
     */
    default Bootstrap getUDPServer(Server server) {
        ChannelHandler channelHandler = protocolMap.get(server.protocol()).getChannelHandler();
        Bootstrap bootstrap = new Bootstrap().group(getWorkerGroup())
                .option(ChannelOption.SO_BROADCAST, true)
                .channel(NioDatagramChannel.class)
                // .handler(loggingHandler)
                .handler(channelHandler);
        return bootstrap;
    }

    T write(ByteBuf byteBuf);

    T close();

}
