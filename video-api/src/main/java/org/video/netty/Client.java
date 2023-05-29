package org.video.netty;

import io.netty.channel.Channel;
import org.video.eum.Protocol;
import org.video.eum.Method;


public interface Client<T> extends Connection<T> {

    String id();

    Protocol protocol();

    Channel channel();

    String url();

    Method methodACK();

}
