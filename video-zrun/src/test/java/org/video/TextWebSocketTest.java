package org.video;

import org.junit.jupiter.api.Test;
import org.video.websocket.WebSocket;

import java.util.concurrent.ExecutionException;

public class TextWebSocketTest extends BaseTest{
    @Test
    public void test() throws ExecutionException, InterruptedException {
        new WebSocket.Builder().setPort(1000).build().get();
    }

}
