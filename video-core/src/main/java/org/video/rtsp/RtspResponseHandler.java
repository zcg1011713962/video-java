package org.video.rtsp;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import lombok.extern.slf4j.Slf4j;
import org.video.manager.ClientManager;
import org.video.netty.ResponseHandler;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RtspResponseHandler extends DefaultRtspMethodHandler<RtspClient> implements ResponseHandler {
    private AtomicInteger count = new AtomicInteger();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("已连接 from {} to {}", ctx.channel().localAddress(), ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            log.info("客户端断开连接 from {} to {}", ctx.channel().localAddress(), ctx.channel().remoteAddress());
        } else {
            log.info("服务端断开连接 from {} to {}", ctx.channel().remoteAddress(), ctx.channel().localAddress());
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DefaultHttpObject msg) throws Exception {
        RtspClient rtspClient = (RtspClient) ClientManager.client(ctx.channel().id().asLongText());
        log.info("消息编号{} from {} to {}", count.incrementAndGet(), ctx.channel().remoteAddress(), ctx.channel().localAddress());
        if (msg instanceof DefaultHttpResponse) {
            DefaultHttpResponse httpResponse = (DefaultHttpResponse) msg;
            int code = httpResponse.status().code();
            HttpHeaders headers = httpResponse.headers();
            log.info("消息编号{} \n{}", count.get(), msg);
            if (code == RtspResponseStatuses.OK.code()) {
                if (StrUtil.isNotBlank(headers.get(RtspHeaderNames.PUBLIC))) {
                    optionsHandler(rtspClient, headers);
                    return;
                }
                if ("application/sdp".equals(headers.get(RtspHeaderNames.CONTENT_TYPE))) {
                    sdpHeaderHandler(rtspClient, headers);
                    return;
                }
                if (StrUtil.isNotBlank(headers.get(RtspHeaderNames.TRANSPORT)) && headers.get(RtspHeaderNames.TRANSPORT).contains("ssrc=")) {
                    setupHandler(rtspClient, headers);
                    return;
                }
            } else {
                if (code == RtspResponseStatuses.UNAUTHORIZED.code()) {
                    describeHandler(rtspClient, headers);
                    return;
                }
            }
        } else if (msg instanceof HttpContent) {
            if (msg instanceof DefaultHttpContent) {
                DefaultHttpContent httpContent = (DefaultHttpContent) msg;
                ByteBuf buf;
                if (httpContent != null && (buf = httpContent.content()) != null) {
                    String sdpFragment = buf.toString(Charset.forName("UTF-8"));
                    log.info("消息编号{} \n{}", count.get(), sdpFragment);
                    sdpCompositeHandler(rtspClient, sdpFragment);
                    return;
                }
            } else if (msg instanceof DefaultLastHttpContent) {
                DefaultLastHttpContent httpContent = (DefaultLastHttpContent) msg;
                ByteBuf buf;
                if (httpContent != null && (buf = httpContent.content()) != null) {
                    String c = buf.toString(Charset.forName("UTF-8"));
                    log.info("消息编号{} \n{}", count.get(), c);
                }
            }
        }
    }

}
