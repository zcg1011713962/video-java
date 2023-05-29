package org.video.netty;


import io.netty.buffer.ByteBuf;
import org.video.eum.Method;

public interface Handle<T> {

    T connect();

    T close();

    T write(ByteBuf byteBuf, Method method);


}

