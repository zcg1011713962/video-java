package org.video;

import lombok.extern.slf4j.Slf4j;
import org.video.rtsp.RtspClient;
import org.video.util.RtspUrlParser;

@Slf4j
public class RtspClientTest {
    public static void main(String[] args) {
        String rtspUrl = "rtsp://admin:link123456@192.168.7.12:554/h264/ch1/main/av_stream";
        RtspUrlParser rtspParser = new RtspUrlParser(rtspUrl);
        if (!rtspParser.parse()) {
            log.error("RtspUrlParser error");
        }
        new RtspClient.Builder().setUrl(rtspUrl).build().thenAccept(f -> {
            log.info("{}", f);
        }).exceptionally(e -> {
            log.error("{}", e);
            return null;
        });
    }
}
