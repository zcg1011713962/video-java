package org.video;

import org.video.rtsp.rtp.RtpServer;

public class UdpServerTest {
    public static void main(String[] args) {
        new RtpServer.Builder().setPort(10999).build();
    }
}
