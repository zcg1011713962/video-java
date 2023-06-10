package org.video.rtsp.entity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RtspReqPacket {
    private static final String userAgent = "LibVLC/3.0.12 (LIVE555 Streaming Media v2016.11.28)";
    public static AtomicInteger commonCseq = new AtomicInteger();
    /**
     * 用于请求服务器所支持的所有方法
     * OPTIONS rtsp://192.168.7.12:554/h264/ch1/main/av_stream RTSP/1.0
     * CSeq: 2
     * User-Agent: LibVLC/3.0.17.4 (LIVE555 Streaming Media v2016.11.28)
     */
    public static ByteBuf options(String uri, int cseq) {
        StringBuffer options = new StringBuffer();
        options.append(RtspMethods.OPTIONS);
        options.append(StringUtil.SPACE);
        options.append(uri);
        options.append(StringUtil.SPACE);
        options.append("RTSP/1.0").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        options.append("CSeq:").append(StringUtil.SPACE).append(cseq).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        options.append("User-Agent:").append(StringUtil.SPACE).append(userAgent).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        options.append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        String message = options.toString();
        log.info("请求报文:\n{}", message);
        ByteBuf buf = Unpooled.buffer(message.getBytes().length);
        buf.writeBytes(message.getBytes());
        return buf;
    }

    /**
     * DESCRIBE rtsp://192.168.7.12:554/h264/ch1/main/av_stream RTSP/1.0
     * CSeq: 3
     * User-Agent: LibVLC/3.0.17.4 (LIVE555 Streaming Media v2016.11.28)
     * Accept: application/sdp
     */
    public static ByteBuf describe(String uri, int cseq) {
        StringBuffer describe = new StringBuffer();
        describe.append(RtspMethods.DESCRIBE);
        describe.append(StringUtil.SPACE);
        describe.append(uri);
        describe.append(StringUtil.SPACE);
        describe.append("RTSP/1.0").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("CSeq:").append(StringUtil.SPACE).append(cseq).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("User-Agent:").append(StringUtil.SPACE).append(userAgent).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("Accept: application/sdp").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        String message = describe.toString();
        log.info("请求报文:\n{}", message);
        ByteBuf buf = Unpooled.buffer(message.getBytes().length);
        buf.writeBytes(message.getBytes());
        return buf;
    }

    /**
     * DESCRIBE rtsp://192.168.7.12:554/h264/ch1/main/av_stream RTSP/1.0
     * CSeq: 4
     * Authorization: Digest username="admin", realm="IP Camera(J2914)", nonce="8fc2dbc69899df7091fc83dcac3cbe3b", uri="rtsp://192.168.7.12:554/h264/ch1/main/av_stream", response="15936484990aa2c25cdfadc8b40ac678"
     * User-Agent: LibVLC/3.0.17.4 (LIVE555 Streaming Media v2016.11.28)
     * Accept: application/sdp
     */
    public static ByteBuf auth(String uri, String userName, String nonce, String realm, String response, int cseq) {
        StringBuffer describe = new StringBuffer();
        describe.append(RtspMethods.DESCRIBE);
        describe.append(StringUtil.SPACE);
        describe.append(uri);
        describe.append(StringUtil.SPACE);
        describe.append("RTSP/1.0").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("CSeq:").append(StringUtil.SPACE).append(cseq).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("User-Agent:").append(StringUtil.SPACE).append(userAgent).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("Authorization: Digest username=\"").append(userName).append("\", realm=\"").append(realm).append("\", nonce=\"").append(nonce).append("\", uri=\"").append(uri).append("\", response=\"").append(response).append("\"").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("Accept: application/sdp").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        String message = describe.toString();
        log.info("请求报文:\n{}", message);
        ByteBuf buf = Unpooled.buffer(message.getBytes().length);
        buf.writeBytes(message.getBytes());
        return buf;
    }

    /**
     * SETUP rtsp://192.168.7.12:554/h264/ch1/main/av_stream/trackID=1 RTSP/1.0
     * CSeq: 5
     * Authorization: Digest username="admin", realm="IP Camera(J2914)", nonce="8fc2dbc69899df7091fc83dcac3cbe3b", uri="rtsp://192.168.7.12:554/h264/ch1/main/av_stream/", response="33474f019bba9ef032d95e65ff580338"
     * User-Agent: LibVLC/3.0.17.4 (LIVE555 Streaming Media v2016.11.28)
     * Transport: RTP/AVP;unicast;client_port=63674-63675
     */
    public static ByteBuf setup(String uri, String transport, String trackID, String session, int cseq) {
        StringBuffer describe = new StringBuffer();
        describe.append(RtspMethods.SETUP);
        describe.append(StringUtil.SPACE);
        describe.append(uri).append("/").append(trackID);
        describe.append(StringUtil.SPACE);
        describe.append("RTSP/1.0").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("CSeq:").append(StringUtil.SPACE).append(cseq).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("User-Agent:").append(StringUtil.SPACE).append(userAgent).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("Transport:").append(StringUtil.SPACE).append(transport).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        if (!StringUtils.isBlank(session)) {
            describe.append("Session:").append(StringUtil.SPACE).append(session).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        }
        describe.append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        String message = describe.toString();
        log.info("请求报文:\n{}", message);
        ByteBuf buf = Unpooled.buffer(message.getBytes().length);
        buf.writeBytes(message.getBytes());
        return buf;
    }

    /**
     * PLAY rtsp://192.168.7.12:554/h264/ch1/main/av_stream/ RTSP/1.0
     * CSeq: 6
     * Authorization: Digest username="admin", realm="IP Camera(J2914)", nonce="8fc2dbc69899df7091fc83dcac3cbe3b", uri="rtsp://192.168.7.12:554/h264/ch1/main/av_stream/", response="fc55bfa7ece3c3e9eeb909ad5c989f1f"
     * User-Agent: LibVLC/3.0.17.4 (LIVE555 Streaming Media v2016.11.28)
     * Session: 285083930
     * Range: npt=0.000-
     */
    public static ByteBuf play(String uri, String session, int cseq) {
        StringBuffer describe = new StringBuffer();
        describe.append(RtspMethods.PLAY);
        describe.append(StringUtil.SPACE);
        describe.append(uri).append("/");
        describe.append(StringUtil.SPACE);
        describe.append("RTSP/1.0").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("CSeq:").append(StringUtil.SPACE).append(cseq).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("User-Agent:").append(StringUtil.SPACE).append(userAgent).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("Session:").append(StringUtil.SPACE).append(session).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("Range:").append(StringUtil.SPACE).append("npt=0.000-").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        String message = describe.toString();
        log.info("请求报文:\n{}", message);
        ByteBuf buf = Unpooled.buffer(message.getBytes().length);
        buf.writeBytes(message.getBytes());
        return buf;
    }

}
