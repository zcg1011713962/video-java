package org.video.rtsp.entity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RtspReqPacket {
    public static ConcurrentMap<String, Boolean> methodACK = new ConcurrentHashMap();
    private static AtomicInteger cseq = new AtomicInteger();
    private static final String userAgent = "LibVLC/3.0.12 (LIVE555 Streaming Media v2016.11.28)";


    static{
        Method[] methods = RtspReqPacket.class.getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                // 是静态方法
                String methodName = method.getName();
                methodACK.put(methodName, false);
            }
        }
    }

    /**
     * OPTIONS 用于请求服务器所支持的所有方法
     * OPTIONS rtsp://wowzaec2demo.streamlock.net:554/vod/mp4:BigBuckBunny_115k.mov RTSP/1.0
     * CSeq: 2
     * User-Agent: LibVLC/3.0.12 (LIVE555 Streaming Media v2016.11.28)
     */
    public static ByteBuf options(String rtspUrl){
        StringBuffer options = new StringBuffer();
        options.append(RtspMethods.OPTIONS);
        options.append(StringUtil.SPACE);
        options.append(rtspUrl);
        options.append(StringUtil.SPACE);
        options.append("RTSP/1.0").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        options.append("CSeq:").append(StringUtil.SPACE).append(cseq.getAndIncrement()).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        options.append("User-Agent:").append(StringUtil.SPACE).append(userAgent).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        options.append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        String message = options.toString();
        log.info("请求报文:{}",message);
        ByteBuf buf = Unpooled.buffer(message.getBytes().length);
        buf.writeBytes(message.getBytes());
        return buf;
    }

    /**
     * DESCRIBE rtsp://wowzaec2demo.streamlock.net:554/vod/mp4:BigBuckBunny_115k.mov RTSP/1.0
     * CSeq: 3
     * User-Agent: LibVLC/3.0.12 (LIVE555 Streaming Media v2016.11.28)
     * Accept: application/sdp
     */
    public static ByteBuf describe(String rtspUrl){
        StringBuffer describe = new StringBuffer();
        describe.append(RtspMethods.DESCRIBE);
        describe.append(StringUtil.SPACE);
        describe.append(rtspUrl);
        describe.append(StringUtil.SPACE);
        describe.append("RTSP/1.0").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("CSeq:").append(StringUtil.SPACE).append(cseq.getAndIncrement()).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("User-Agent:").append(StringUtil.SPACE).append(userAgent).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("Accept: application/sdp").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        String message = describe.toString();
        log.info("请求报文:{}",message);
        ByteBuf buf = Unpooled.buffer(message.getBytes().length);
        buf.writeBytes(message.getBytes());
        return buf;
    }

    public static ByteBuf setup(String rtspUrl, String transport, String trackID, String session){
        StringBuffer describe = new StringBuffer();
        describe.append(RtspMethods.SETUP);
        describe.append(StringUtil.SPACE);
        describe.append(rtspUrl).append("/").append(trackID);
        describe.append(StringUtil.SPACE);
        describe.append("RTSP/1.0").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("CSeq:").append(StringUtil.SPACE).append(cseq.getAndIncrement()).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("User-Agent:").append(StringUtil.SPACE).append(userAgent).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("Transport:").append(StringUtil.SPACE).append(transport).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        if(!StringUtils.isBlank(session)){
            describe.append("Session:").append(StringUtil.SPACE).append(session).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        }
        describe.append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        String message = describe.toString();
        log.info("请求报文:{}",message);
        ByteBuf buf = Unpooled.buffer(message.getBytes().length);
        buf.writeBytes(message.getBytes());
        return buf;
    }

    public static ByteBuf play(String rtspUrl, String session){
        StringBuffer describe = new StringBuffer();
        describe.append(RtspMethods.PLAY);
        describe.append(StringUtil.SPACE);
        describe.append(rtspUrl).append("/");
        describe.append(StringUtil.SPACE);
        describe.append("RTSP/1.0").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("CSeq:").append(StringUtil.SPACE).append(cseq.getAndIncrement()).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("User-Agent:").append(StringUtil.SPACE).append(userAgent).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("Session:").append(StringUtil.SPACE).append(session).append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append("Range:").append(StringUtil.SPACE).append("npt=0.000-").append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        describe.append(StringUtil.CARRIAGE_RETURN).append(StringUtil.LINE_FEED);
        String message = describe.toString();
        log.info("请求报文:{}",message);
        ByteBuf buf = Unpooled.buffer(message.getBytes().length);
        buf.writeBytes(message.getBytes());
        return buf;
    }

}
