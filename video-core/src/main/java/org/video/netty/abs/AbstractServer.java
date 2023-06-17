package org.video.netty.abs;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.video.eum.Protocol;
import org.video.exception.BaseException;
import org.video.manager.CacheManager;
import org.video.manager.EventLoopGroupManager;
import org.video.netty.Server;

import java.util.Optional;

public abstract class AbstractServer<T> implements Server<T> {
    private static Bootstrap bootstrap;
    private ChannelHandler channelHandler;

    protected void channelHandler(Protocol protocol) {
        Optional<ChannelHandler> optional = CacheManager.protocolTable().row(protocol).keySet().stream().findFirst();
        if(optional.isPresent()){
            channelHandler = optional.get();
            return;
        }
        throw new BaseException("获取channelHandler失败");
    }

    @Override
    public EventLoopGroup getWorkerGroup() {
        return EventLoopGroupManager.getWorkGroup();
    }

    @Override
    public Bootstrap getUDPServer() {
        if(bootstrap != null){
            return bootstrap;
        }
        bootstrap = new Bootstrap().group(getWorkerGroup())
                .option(ChannelOption.SO_BROADCAST, true)
                .channel(NioDatagramChannel.class)
                // .handler(loggingHandler)
                .handler(channelHandler);
        return bootstrap;
    }

}
