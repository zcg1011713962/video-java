package org.video.websocket;

import io.netty.bootstrap.Bootstrap;
import org.video.entity.response.BaseResponse;
import org.video.eum.Protocol;
import org.video.manager.ServerManager;
import org.video.netty.abs.AbstractServer;

import java.util.concurrent.CompletableFuture;

public class WebSocket extends AbstractServer<CompletableFuture<BaseResponse>> {

    private WebSocket(int port) {
        super(port);
    }

    @Override
    public Protocol protocol() {
        return Protocol.WEBSOCKET;
    }

    @Override
    public void manager() {
        channelHandler(protocol());
        ServerManager.put(id(), this);
    }

    @Override
    public CompletableFuture<BaseResponse> init() {
        manager();
        Bootstrap bootstrap = getWebSocketServer();
        return bind(bootstrap);
    }

    public static class Builder {
        private int port;

        public WebSocket.Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public CompletableFuture<BaseResponse> build() {
            WebSocket webSocket = new WebSocket(port);
            return webSocket.init();
        }

    }


}
