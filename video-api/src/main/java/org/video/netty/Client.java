package org.video.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import org.video.eum.Protocol;
import org.video.eum.Method;


public interface Client<T> extends Connection<T> {

    /**
     * tcp client
     * @return
     */
    default Bootstrap getTcpClient() {
        return null;
    }

}
