package org.video.rtsp.rtp;

import cn.hutool.core.lang.UUID;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.video.eum.Protocol;
import org.video.exception.BaseException;
import org.video.manager.CacheManager;
import org.video.netty.ServerManager;
import org.video.netty.abs.AbstractServer;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
@Slf4j
public class RtpServer extends AbstractServer<CompletableFuture<Boolean>> {
    private int port;
    private Channel channel;


    private RtpServer(int port){
        super.channelHandler(protocol());
        this.port = port;
    }

    @Override
    public String id() {
        return UUID.fastUUID().toString();
    }

    @Override
    public Protocol protocol() {
        return Protocol.RTP;
    }

    @Override
    public Channel channel() {
        if (channel != null && channel.isOpen()) {
            return channel;
        }
        return null;
    }

    @Override
    public CompletableFuture<Boolean> init() {
        ServerManager.put(id(), this);
        return bind();
    }

    @Override
    public CompletableFuture<Boolean> bind() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        try {
            Bootstrap udpServer = getUDPServer();
            udpServer.bind(port).sync().addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    channel = f.channel();
                    log.info("成功监听UDP端口-{}", port);
                    completableFuture.complete(true);
                } else {
                    completableFuture.completeExceptionally(f.cause());
                }
            });
        }catch (Exception e){
            log.error("{}", e.getMessage());
            completableFuture.completeExceptionally(e.getCause());
        }
        return completableFuture;
    }

    @Override
    public CompletableFuture<Boolean> write(ByteBuf byteBuf) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> close() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        channel.close().addListener((ChannelFutureListener)f ->{
            if(f.isSuccess()){
                if(Objects.isNull(ServerManager.remove(id()))){
                    completableFuture.complete(true);
                }
                completableFuture.completeExceptionally(new BaseException("ServerManager 移除服务端缓存失败" + id()));
            }
            completableFuture.completeExceptionally(f.cause());
        });
        return completableFuture;
    }


    public static class Builder {
        private int port;

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public CompletableFuture<Boolean> build() {
            RtpServer rtpServer = new RtpServer(port);
            return rtpServer.init();
        }

    }

}
