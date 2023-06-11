package org.video;

import lombok.extern.slf4j.Slf4j;
import org.video.rtsp.RtspClient;
import org.video.util.RtspUrlParser;

@Slf4j
public class RtspClientTest {
    public static void main(String[] args) throws InterruptedException {
        String rtspUrl = "rtsp://admin:link123456@192.168.7.12:554/h264/ch1/main/av_stream";
        RtspUrlParser rtspParser = new RtspUrlParser(rtspUrl);
        if (rtspParser.parse()) {
            new RtspClient.Builder().setUrl(rtspUrl).build();
        } else {
            log.info("RtspUrlParser error");
        }
    }
}
