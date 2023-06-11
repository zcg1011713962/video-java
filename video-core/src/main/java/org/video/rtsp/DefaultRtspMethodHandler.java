package org.video.rtsp;

import cn.hutool.core.util.StrUtil;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspMethods;
import lombok.extern.slf4j.Slf4j;
import org.video.exception.BaseException;
import org.video.rtsp.entity.RtspEntity;
import org.video.rtsp.entity.RtspReqPacket;
import org.video.rtsp.rtp.RtpServer;
import org.video.util.RTSPDigest;
import org.video.util.RtspSDParser;
import org.video.util.RtspUtil;

import java.util.Map;
@Slf4j
public abstract class DefaultRtspMethodHandler<T> extends RtspMethodHandler<RtspClient>{

    @Override
    public void optionsHandler(RtspClient rtspClient, HttpHeaders headers) {
        int cseq = Integer.parseInt(headers.get(RtspHeaderNames.CSEQ)) + 1;
        rtspClient.write(RtspReqPacket.describe(rtspClient.getRtspEntity().getUri(), cseq));
    }

    @Override
    public void describeHandler(RtspClient rtspClient, HttpHeaders headers) {
        // WWW-Authenticate -> Digest realm="IP Camera(J2914)", nonce="446c8be9ca7682b00f43b2bae7e98b86", stale="FALSE"
        int cseq = Integer.parseInt(headers.get(RtspHeaderNames.CSEQ)) + 1;
        String authenticate = headers.get("WWW-Authenticate");
        Map<String, String> authMap = RtspUtil.decode(authenticate);
        RtspEntity rtspEntity = rtspClient.getRtspEntity();
        rtspEntity.setNonce(authMap.get("nonce"));
        rtspEntity.setRealm(authMap.get("realm"));
        RTSPDigest digest = new RTSPDigest(rtspEntity.getUserName(), rtspEntity.getRealm(), rtspEntity.getNonce(), rtspEntity.getUri(), RtspMethods.DESCRIBE.name(), rtspEntity.getPassword());
        String response = digest.calculateResponse();
        rtspClient.write(RtspReqPacket.auth(rtspEntity.getUri(), rtspEntity.getUserName(), authMap.get("nonce"), authMap.get("realm"), response,  cseq));
    }

    @Override
    public void sdpHeaderHandler(RtspClient rtspClient, HttpHeaders headers) {
        String contentLength = headers.get(RtspHeaderNames.CONTENT_LENGTH);
        int cseq = Integer.parseInt(headers.get(RtspHeaderNames.CSEQ)) + 1;
        rtspClient.getRtspSDParser().setContentLength(Integer.parseInt(contentLength));
        rtspClient.getRtspSDParser().setCseq(cseq);
    }

    @Override
    public void sdpCompositeHandler(RtspClient rtspClient, String sdpFragment) {
        RtspSDParser rtspSDParser = rtspClient.getRtspSDParser();
        boolean ret = rtspSDParser.append(sdpFragment).parser();
        if (ret) {
            log.info("解析SDP \n{}", rtspClient.getRtspSDParser());
            RtspEntity rtspEntity = rtspClient.getRtspEntity();
            RTSPDigest digest = new RTSPDigest(rtspEntity.getUserName(), rtspEntity.getRealm(), rtspEntity.getNonce(), rtspEntity.getUri(), RtspMethods.SETUP.name(), rtspEntity.getPassword());
            String response = digest.calculateResponse();
            int rtpPort = 62054;
            int rtcpPort = 62055;
            new RtpServer.Builder().setPort(rtpPort).build().thenAccept(success ->{
                if(success){
                    rtspClient.write(RtspReqPacket.setup(rtspEntity.getUri(), rtspSDParser.getTransport(), rtpPort, rtcpPort, rtspSDParser.getTrackID(), StrUtil.EMPTY, rtspEntity.getUserName(), rtspEntity.getNonce(), rtspEntity.getRealm(), response, rtspClient.getRtspSDParser().getCseq()));
                }
            }).exceptionally(f ->{
                log.error("{}", f.getMessage());
                return null;
            });
        }
    }

    @Override
    public void setupHandler(RtspClient rtspClient, HttpHeaders headers) {
        String sessionLine = headers.get(RtspHeaderNames.SESSION);
        int cseq = Integer.parseInt(headers.get(RtspHeaderNames.CSEQ)) + 1;
        if(StrUtil.isBlank(sessionLine)){
            throw new BaseException("setup session return null");
        }
        String [] s = sessionLine.split(";");
        String session = s[0].trim();
        String timeout = s[1].split("=")[1].trim();
        rtspClient.write(RtspReqPacket.play(rtspClient.getRtspEntity().getUri(), session, cseq));
    }
}
