package org.video.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.video.entity.request.web.RtspRequest;
import org.video.entity.response.BaseResponse;
import org.video.manager.ClientManager;
import org.video.rtsp.RtspClient;
import org.video.rtsp.entity.RtspReqPacket;
import org.video.rtsp.entity.RtspUrlParser;
import org.video.service.RtspService;

import java.util.concurrent.CompletableFuture;
@Slf4j
@Component
public class RtspServiceImpl implements RtspService<RtspRequest> {


    @Override
    public CompletableFuture<BaseResponse> connect(RtspRequest rtspRequest) {
        RtspUrlParser rtspParser = new RtspUrlParser(rtspRequest.getUrl());
        if (!rtspParser.parse()) {
            log.error("RtspUrlParser error");
            return CompletableFuture.completedFuture(BaseResponse.exception());
        }
        return new RtspClient.Builder().setUrl(rtspRequest.getUrl()).build();
    }

    @Override
    public CompletableFuture<BaseResponse> disconnect(RtspRequest rtspRequest) {
        RtspClient conn = (RtspClient) ClientManager.get(rtspRequest.getClientId());
        return conn.write(RtspReqPacket.teardown(conn.getRtspEntity().getUri(), conn.getRtspSDParser().getSession(), conn.getRtspSDParser().getLastCseq()));
    }
}
