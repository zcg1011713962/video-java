package org.video.netty;

import io.netty.channel.Channel;
import org.video.eum.Protocol;

public interface Server<T> extends Connection<T> {

    String id();

    Protocol protocol();

    Channel channel();

}
