package org.video.netty;


import io.netty.bootstrap.Bootstrap;

public interface ServerHandle<T> extends Handle{

    T init();

    default T bind(Bootstrap bootstrap){
        return null;
    }

}

