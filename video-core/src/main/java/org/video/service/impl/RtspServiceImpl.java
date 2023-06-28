package org.video.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.video.entity.request.web.RtspRequest;
import org.video.entity.response.BaseResponse;
import org.video.rtsp.RtspClient;
import org.video.rtsp.entity.RtspUrlParser;
import org.video.service.RtspService;

import java.util.concurrent.CompletableFuture;
@Slf4j
@Component
public class RtspServiceImpl implements RtspService<RtspRequest> {


    @Override
    public CompletableFuture<BaseResponse> request(RtspRequest rtspRequest) {
        RtspUrlParser rtspParser = new RtspUrlParser(rtspRequest.getUrl());
        if (!rtspParser.parse()) {
            log.error("RtspUrlParser error");
            return CompletableFuture.completedFuture(BaseResponse.exception());
        }
        return new RtspClient.Builder().setUrl(rtspRequest.getUrl()).build();
    }
}
