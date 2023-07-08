package org.video.intercept;

import cn.hutool.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.video.entity.response.BaseResponse;
import org.video.exception.BaseException;

@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = Exception.class)
    public BaseResponse handleMyException(Exception ex) {
        log.error("全局异常捕获:{}", ex.getMessage());
        if(ex instanceof BaseException){
            BaseException e = (BaseException) ex;
            return new BaseResponse.Builder()
                    .code(e.getCode())
                    .message(e.getMessage())
                    .build();
        }
        return new BaseResponse.Builder()
                .code(HttpStatus.HTTP_INTERNAL_ERROR)
                .message(ex.getMessage())
                .build();
    }

}
