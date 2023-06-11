package org.video.netty;


public interface ClientHandle<T> extends Handle{

    default T connect(){
        return null;
    }


}

