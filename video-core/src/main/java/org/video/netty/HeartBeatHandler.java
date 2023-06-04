package org.video.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳检测
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 超时事件
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvent = (IdleStateEvent) evt;
            if (idleEvent.state() == IdleState.READER_IDLE) {
                log.error("触发读超时事件");
            } else if (idleEvent.state() == IdleState.WRITER_IDLE) {
                log.error("触发写超时事件");
            } else if (idleEvent.state() == IdleState.ALL_IDLE) {
                log.error("触发超时事件");
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
