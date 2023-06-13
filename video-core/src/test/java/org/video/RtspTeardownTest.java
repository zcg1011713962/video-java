package org.video;

import org.common.util.Md5Util;
import org.video.netty.ClientManager;
import org.video.rtsp.RtspClient;
import org.video.rtsp.entity.RtspReqPacket;
import org.video.util.RtspUrlParser;
import org.video.util.ThreadPoolUtil;

import java.util.concurrent.TimeUnit;

public class RtspTeardownTest {

    public static void main(String[] args) {
        String rtspUrl = "rtsp://admin:link123456@192.168.7.12:554/h264/ch1/main/av_stream";
        /*RtspUrlParser rtspParser = new RtspUrlParser(rtspUrl);
        RtspClient rtspClient = (RtspClient) ClientManager.get(Md5Util.calculateMD5(rtspUrl));
        rtspClient.write(RtspReqPacket.teardown(rtspParser.getUri(), "11111", 5));*/
        ThreadPoolUtil.videoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("---");
            }
        });

        ThreadPoolUtil.videoSchedulerExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("-----------");
            }
        },20, TimeUnit.SECONDS);

    }

}
