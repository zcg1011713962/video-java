package org.video.websocket.handler.abs;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.video.netty.abs.AbstractInboundHandler;

@Slf4j
public class TextWebSocketHandler extends AbstractInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("TextWebSocketFrameHandler:{}", msg.text());
    }


}
