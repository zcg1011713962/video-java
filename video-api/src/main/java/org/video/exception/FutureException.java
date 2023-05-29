package org.video.exception;

import java.io.Serializable;

public class FutureException extends RuntimeException implements Serializable {
    private int code = 10000;
    private String message;

    public FutureException(String message) {
        super(message);
    }

    public FutureException(int code, String message) {
        super(message);
        this.code = code;
    }

}
