package org.video.rtsp;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.common.util.Md5Util;
import org.video.eum.Protocol;
import org.video.exception.BaseException;
import org.video.exception.FutureException;
import org.video.netty.Client;
import org.video.netty.ClientManager;
import org.video.rtsp.entity.RtspEntity;
import org.video.rtsp.entity.RtspReqPacket;
import org.video.rtsp.init.RtspClientlInitializer;
import org.video.util.RtspSDParser;
import org.video.util.RtspUrlParser;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
@Slf4j
public class RtspClient<T> extends RtspClientlInitializer implements Client<CompletableFuture<Boolean>> {
    private String clientId;
    private String url;
    private Channel channel;
    private RtspEntity rtspEntity;
    private RtspSDParser rtspSDParser = new RtspSDParser();

    private RtspClient(String url, boolean proxy, String clientId) {
        super(proxy);
        this.url = url;
        this.clientId = clientId;
    }

    @Override
    public CompletableFuture<Boolean> init() {
        RtspUrlParser rtspParser = new RtspUrlParser(url);
        if (rtspParser.parse()) {
            rtspEntity = new RtspEntity(this, rtspParser.getUri(), rtspParser.getUsername(), rtspParser.getPassword());
            protocolMap.put(protocol(), rtspEntity);
            ClientManager.put(id(), this);
        } else {
            CompletableFuture.completedFuture(new BaseException("解析url错误"));
        }
        return connect().thenApply(success -> {
            if (success) {
                write(RtspReqPacket.options(rtspParser.getUri(), RtspReqPacket.commonCseq.getAndIncrement()));
                return true;
            }
            return false;
        });
    }

    @Override
    public String id() {
        if(StrUtil.isBlank(clientId)){
            return Md5Util.calculateMD5(url);
        }
        return clientId;
    }

    @Override
    public Protocol protocol() {
        return Protocol.RTSP;
    }

    @Override
    public Channel channel() {
        if (channel != null && channel.isOpen()) {
            return channel;
        }
        return null;
    }


    public RtspSDParser getRtspSDParser() {
        return rtspSDParser;
    }

    public RtspEntity getRtspEntity() {
        return rtspEntity;
    }

    public void setRtspEntity(RtspEntity rtspEntity) {
        this.rtspEntity = rtspEntity;
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
    public CompletableFuture<Boolean> write(ByteBuf byteBuf) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (channel != null && channel.isOpen()) {
            channel.writeAndFlush(byteBuf).addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    completableFuture.complete(true);
                } else {
                    completableFuture.completeExceptionally(f.cause());
                }
            });
        }
        return completableFuture;
    }

    public static class Builder {
        private String url;
        private String clientId;
        private boolean proxy;

        /**
         * @param url rtsp://admin:link123456@192.168.7.12:554/h264/ch1/main/av_stream
         * @return
         */
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setProxy(boolean proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public CompletableFuture<Boolean> build() {
            RtspClient rtspClient = new RtspClient(url, proxy, clientId);
            CompletableFuture future = rtspClient.init();
            return future;
        }

    }

}
