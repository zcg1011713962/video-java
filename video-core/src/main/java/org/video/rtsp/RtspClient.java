package org.video.rtsp;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.common.util.Md5Util;
import org.video.entity.response.BaseResponse;
import org.video.eum.Protocol;
import org.video.exception.BaseException;
import org.video.exception.FutureException;
import org.video.manager.ClientManager;
import org.video.netty.abs.AbstractClient;
import org.video.rtsp.entity.RtspEntity;
import org.video.rtsp.entity.RtspReqPacket;
import org.video.rtsp.entity.RtspSDParser;
import org.video.rtsp.entity.RtspUrlParser;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
@Slf4j
public class RtspClient<T> extends AbstractClient<CompletableFuture<BaseResponse>> {
    private String clientId;
    private String url;
    private Channel channel;
    private RtspEntity rtspEntity;
    private RtspSDParser rtspSDParser = new RtspSDParser();

    private RtspClient(String url, String clientId) {
        super.channelHandler(protocol());
        this.url = url;
        this.clientId = clientId;
    }

    @Override
    public CompletableFuture<BaseResponse> init() {
        RtspUrlParser rtspParser = new RtspUrlParser(url);
        if (rtspParser.parse()) {
            rtspEntity = new RtspEntity(rtspParser.getUri(), rtspParser.getUsername(), rtspParser.getPassword());
            ClientManager.put(id(), this);
        } else {
            CompletableFuture.completedFuture(new BaseException("解析url错误"));
        }
        return connect().thenApply(success -> {
            if (success.getCode() == HttpStatus.HTTP_OK) {
                write(RtspReqPacket.options(rtspParser.getUri(), RtspReqPacket.commonCseq.getAndIncrement()));
                HashMap<String, String> params = new HashMap<>(1);
                params.put("clientId", id());
                return BaseResponse.success(params);
            }
            return BaseResponse.fail();
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
        if (channel != null) {
            return channel;
        }
        throw new BaseException("通道为空");
    }


    public RtspSDParser getRtspSDParser() {
        return rtspSDParser;
    }

    public RtspEntity getRtspEntity() {
        return rtspEntity;
    }

    @Override
    public CompletableFuture<BaseResponse> connect() {
        CompletableFuture<BaseResponse> cFuture = new CompletableFuture<>();
        try {
            RtspUrlParser rtspUrlParser = new RtspUrlParser(url);
            if (!rtspUrlParser.parse()) {
                cFuture.completeExceptionally(new FutureException("检查RTSP地址"));
            }
            getTcpClient().connect(new InetSocketAddress(rtspUrlParser.getIp(), rtspUrlParser.getPort())).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    channel = future.channel();
                    cFuture.complete(BaseResponse.success());
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
    public CompletableFuture<BaseResponse> close() {
        CompletableFuture<BaseResponse> completableFuture = new CompletableFuture<>();
        channel.close().addListener((ChannelFutureListener)f ->{
            if(f.isSuccess()){
                if(Objects.isNull(ClientManager.remove(id()))){
                    completableFuture.complete(BaseResponse.success());
                }
                completableFuture.completeExceptionally(new BaseException("ClientManager 移除客户端缓存失败" + id()));
            }
            completableFuture.completeExceptionally(f.cause());
        });
        return completableFuture;
    }

    @Override
    public CompletableFuture<BaseResponse> write(ByteBuf byteBuf) {
        if (channel != null && channel.isOpen()) {
            CompletableFuture<BaseResponse> completableFuture = new CompletableFuture<>();
            channel.writeAndFlush(byteBuf).addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    completableFuture.complete(BaseResponse.success());
                } else {
                    completableFuture.completeExceptionally(f.cause());
                }
            });
            return completableFuture;
        }
        throw new BaseException("通道不可用");
    }

    public static class Builder {
        private String url;
        private String clientId;

        /**
         * @param url rtsp://admin:link123456@192.168.7.12:554/h264/ch1/main/av_stream
         * @return
         */
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public CompletableFuture<BaseResponse> build() {
            return new RtspClient(url, clientId).init();
        }

    }

}
