package org.video;

import org.junit.jupiter.api.Test;
import org.video.rtsp.rtp.RtpServer;

import java.util.concurrent.ExecutionException;

public class UdpServerTest extends BaseTest {

    @Test
    public void udpServerTest() throws ExecutionException, InterruptedException {
        new RtpServer.Builder().setPort(10999).build().get();
    }

}
