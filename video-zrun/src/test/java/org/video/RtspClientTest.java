package org.video;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.video.manager.EventLoopGroupManager;
import org.video.rtsp.RtspClient;
import org.video.rtsp.entity.RtspUrlParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RtspClientTest extends BaseTest {

    @Test
    public void rtspConnect() throws ExecutionException, InterruptedException {
        String rtspUrl = "rtsp://admin:link123456@192.168.7.12:554/h264/ch1/main/av_stream";
        //String rtspUrl = "rtsp://127.0.0.1:554/hls/e16f167da33faa2e1c4091b3cf337c79";
        RtspUrlParser rtspParser = new RtspUrlParser(rtspUrl);
        if (!rtspParser.parse()) {
            log.error("RtspUrlParser error");
            return;
        }
        new RtspClient.Builder().setUrl(rtspUrl).build().thenAccept(f -> {
            log.info("{}", f);
        }).exceptionally(e -> {
            log.error("{}", e);
            EventLoopGroupManager.getBossGroup().shutdownGracefully();
            EventLoopGroupManager.getWorkGroup().shutdownGracefully();
            return null;
        }).get();
        TimeUnit.MINUTES.sleep(2);
    }
}
