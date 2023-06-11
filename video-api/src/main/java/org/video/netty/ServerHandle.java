package org.video.netty;


public interface ServerHandle<T> extends Handle{

    T init();

    default T bind(){
        return null;
    }

}

