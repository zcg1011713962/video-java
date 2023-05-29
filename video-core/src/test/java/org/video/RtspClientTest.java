package org.video;

import lombok.extern.slf4j.Slf4j;
import org.video.eum.Method;
import org.video.rtsp.RtspClient;
import org.video.rtsp.entity.RtspReqPacket;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class RtspClientTest {
    public static void main(String[] args) throws InterruptedException {
        String rtspUrl = "rtsp://spt:ncdpz2023@120.237.202.212:554/h264/ch1/main/av_stream";
        RtspClient rtspClient = new RtspClient.Builder().setUrl(rtspUrl).build();
        CompletableFuture<Boolean> cFuture = rtspClient.connect();
        cFuture.thenAccept(success ->{
           if(success){
               rtspClient.write(RtspReqPacket.options(rtspUrl), Method.OPTIONS);
           }
        }).exceptionally(e ->{
            log.error("{}", e.getMessage());
            return null;
        });
    }
}
