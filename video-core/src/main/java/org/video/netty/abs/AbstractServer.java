package org.video.netty.abs;

import cn.hutool.core.lang.UUID;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.video.entity.response.BaseResponse;
import org.video.eum.Protocol;
import org.video.exception.BaseException;
import org.video.manager.CacheManager;
import org.video.manager.EventLoopGroupManager;
import org.video.manager.ServerManager;
import org.video.netty.Server;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
@Slf4j
public abstract class AbstractServer<T> implements Server<CompletableFuture<BaseResponse>> {
    private static volatile Bootstrap udpBootstrap;
    private static volatile Bootstrap websocketBootstrap;
    private ChannelHandler channelHandler;
    private String id;
    protected int port;
    protected Channel channel;

    public AbstractServer(int port) {
        this.id = UUID.fastUUID().toString();
        this.port = port;
    }

    protected void channelHandler(Protocol protocol) {
        Optional<ChannelHandler> optional = CacheManager.protocolTable().row(protocol).keySet().stream().findFirst();
        if (optional.isPresent()) {
            channelHandler = optional.get();
            return;
        }
        throw new BaseException("获取channelHandler失败");
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Channel channel() {
        if (channel != null && channel.isOpen()) {
            return channel;
        }
        return null;
    }

    @Override
    public EventLoopGroup getWorkerGroup() {
        return EventLoopGroupManager.getWorkGroup();
    }

    @Override
    public Bootstrap getUDPServer() {
        if (udpBootstrap == null) {
            synchronized (Server.class) {
                if (udpBootstrap == null) {
                    udpBootstrap = new Bootstrap().group(getWorkerGroup())
                            .option(ChannelOption.SO_BROADCAST, true)
                            .channel(NioDatagramChannel.class)
                            .handler(channelHandler);
                }
            }
        }
        return udpBootstrap;
    }

    @Override
    public Bootstrap getWebSocketServer() {
        if (websocketBootstrap == null) {
            synchronized (Server.class) {
                if (websocketBootstrap == null) {
                    websocketBootstrap = new Bootstrap().group(getWorkerGroup())
                            .channel(NioServerSocketChannel.class)
                            .handler(channelHandler);
                }
            }
        }
        return websocketBootstrap;
    }

    @Override
    public CompletableFuture<BaseResponse> bind(Bootstrap bootstrap) {
        CompletableFuture<BaseResponse> completableFuture = new CompletableFuture<>();
        try {
            bootstrap.bind(port).sync().addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    channel = f.channel();
                    log.info("------成功监听端口-------{}", port);
                    completableFuture.complete(BaseResponse.success());
                } else {
                    completableFuture.completeExceptionally(f.cause());
                }
            }).get();
        }catch (Exception e){
            log.error("{}", e.getMessage());
            completableFuture.completeExceptionally(e.getCause());
        }
        return completableFuture;
    }

    @Override
    public CompletableFuture<BaseResponse> write(ByteBuf byteBuf) {
        return null;
    }

    @Override
    public CompletableFuture<BaseResponse> close() {
        CompletableFuture<BaseResponse> completableFuture = new CompletableFuture<>();
        channel.close().addListener((ChannelFutureListener) f ->{
            if(f.isSuccess()){
                if(Objects.isNull(ServerManager.remove(id()))){
                    completableFuture.complete(BaseResponse.success());
                }
                completableFuture.completeExceptionally(new BaseException("ServerManager 移除服务端缓存失败" + id()));
            }
            completableFuture.completeExceptionally(f.cause());
        });
        return completableFuture;
    }


}
