package org.video.service;

import org.video.entity.response.BaseResponse;

import java.util.concurrent.CompletableFuture;

public interface RtspService<T> {

    CompletableFuture<BaseResponse> request(T t);

}
