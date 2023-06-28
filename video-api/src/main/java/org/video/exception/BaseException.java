package org.video.exception;

import lombok.Data;

import java.io.Serializable;
@Data
public class BaseException extends RuntimeException implements Serializable {

    private int code = 10000;
    private String message;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

}
