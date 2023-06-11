package org.video.rtsp.rtp;

import cn.hutool.core.lang.UUID;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.video.eum.Protocol;
import org.video.netty.Server;
import org.video.netty.ServerManager;
import org.video.rtsp.entity.RtpEntity;
import org.video.rtsp.init.RtpServerlInitializer;

import java.util.concurrent.CompletableFuture;
@Slf4j
public class RtpServer extends RtpServerlInitializer implements Server<CompletableFuture<Boolean>> {
    private int port;
    private Channel channel;

    private RtpServer(int port){
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
        RtpEntity rtpEntity = new RtpEntity(this);
        protocolMap.put(protocol(), rtpEntity);
        ServerManager.put(id(), this);
        return bind();
    }

    @Override
    public CompletableFuture<Boolean> bind() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        try {
            Bootstrap udpServer = getUDPServer(this);
            udpServer.bind(port).sync().addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    channel = f.channel();
                    log.info("bind udp-{} success", port);
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
        return null;
    }


    public static class Builder {
        private int port;

        public RtpServer.Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public CompletableFuture<Boolean> build() {
            RtpServer rtpServer = new RtpServer(port);
            return rtpServer.init();
        }

    }

}
