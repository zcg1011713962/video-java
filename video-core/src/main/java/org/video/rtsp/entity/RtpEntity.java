package org.video.rtsp.entity;

import io.netty.channel.ChannelHandler;
import org.video.entity.BaseEntity;

public class RtpEntity extends BaseEntity {
    public RtpEntity(ChannelHandler channelHandler) {
        super(channelHandler);
    }
}
