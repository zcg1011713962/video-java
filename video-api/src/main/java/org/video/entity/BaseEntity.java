package org.video.entity;

import io.netty.channel.ChannelHandler;

public class BaseEntity {

    protected ChannelHandler channelHandler;

    public BaseEntity(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }

    public void setChannelHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }
}
