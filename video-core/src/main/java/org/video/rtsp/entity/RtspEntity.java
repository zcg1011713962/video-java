package org.video.rtsp.entity;

import io.netty.channel.ChannelHandler;
import org.video.entity.BaseEntity;

public class RtspEntity extends BaseEntity {

    private String url;

    public RtspEntity(ChannelHandler channelHandler, String url) {
        super(channelHandler);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
