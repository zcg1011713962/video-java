package org.video.rtsp;

import cn.hutool.core.lang.UUID;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.video.eum.Method;
import org.video.eum.Protocol;
import org.video.exception.BaseException;
import org.video.exception.FutureException;
import org.video.netty.Client;
import org.video.netty.ClientManager;
import org.video.rtsp.entity.RtspEntity;
import org.video.rtsp.init.RtspClientlInitializer;
import org.video.util.RtspUrlParser;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class RtspClient<T> extends RtspClientlInitializer implements Client<CompletableFuture<Boolean>> {
    private String url;
    private Channel channel;
    private Method methodACK;

    private RtspClient() {

    }

    private void init(String url) {
        this.url = url;
        protocolMap.put(protocol(), new RtspEntity(this, url));
        ClientManager.put(id(), this);
    }

    @Override
    public String id() {
        return UUID.fastUUID().toString();
    }

    @Override
    public Protocol protocol() {
        return Protocol.RTSP;
    }

    @Override
    public Channel channel() {
        if(channel !=null && channel.isOpen()){
            return channel;
        }
        return null;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String uri() {
        RtspUrlParser p = new RtspUrlParser(url);
        if(p.parse()){
            return p.getUri();
        }
        throw new BaseException("RtspUrlParser error");
    }

    @Override
    public Method methodACK() {
        return methodACK;
    }

    @Override
    public CompletableFuture<Boolean> connect() {
        CompletableFuture<Boolean> cFuture = new CompletableFuture<>();
        try {
            RtspUrlParser rtspUrlParser = new RtspUrlParser(url);
            if (!rtspUrlParser.parse()) {
                cFuture.completeExceptionally(new FutureException("检查RTSP地址"));
            }
            getBootstrap(this).connect(new InetSocketAddress(rtspUrlParser.getIp(), rtspUrlParser.getPort())).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    channel = future.channel();
                    cFuture.complete(true);
                } else {
                    cFuture.completeExceptionally(future.cause());
                }
            });
        } catch (Exception e) {
            cFuture.completeExceptionally(e);
        }
        return cFuture;
    }

    @Override
    public CompletableFuture<Boolean> close() {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> write(ByteBuf byteBuf, Method method) {
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(byteBuf).addListener((ChannelFutureListener) f->{
                if(f.isSuccess()){
                    methodACK = method;
                }else{
                    methodACK = Method.FIN;
                }
            });
        }
        return CompletableFuture.completedFuture(false);
    }

    public static class Builder {
        private String url;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public RtspClient build() {
            RtspClient rtspClient = new RtspClient();
            rtspClient.init(url);
            return rtspClient;
        }

    }

}
