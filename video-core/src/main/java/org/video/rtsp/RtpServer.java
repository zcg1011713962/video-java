package org.video.rtsp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.video.entity.response.BaseResponse;
import org.video.eum.Protocol;
import org.video.exception.BaseException;
import org.video.manager.ServerManager;
import org.video.netty.abs.AbstractServer;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
@Slf4j
public class RtpServer extends AbstractServer<CompletableFuture<BaseResponse>> {

    private RtpServer(int port){
        super(port);
    }


    @Override
    public Protocol protocol() {
        return Protocol.RTP;
    }

    @Override
    public void manager() {
        channelHandler(protocol());
        ServerManager.put(id(), this);
    }


    @Override
    public CompletableFuture<BaseResponse> init() {
        manager();
        channelHandler(protocol());
        Bootstrap udpServer = getUDPServer();
        return bind(udpServer);
    }

    public static class Builder {
        private int port;

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public CompletableFuture<BaseResponse> build() {
            RtpServer rtpServer = new RtpServer(port);
            return rtpServer.init();
        }

    }

}
