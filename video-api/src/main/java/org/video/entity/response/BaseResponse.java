package org.video.entity.response;

import cn.hutool.http.HttpStatus;

public class BaseResponse<T> {
    private int code;
    private String message;
    private T data;

    private static BaseResponse success = new BaseResponse("success", HttpStatus.HTTP_OK);
    private static BaseResponse fail = new BaseResponse("fail", 1000);
    private static BaseResponse exception = new BaseResponse("exception", 2000);

    private BaseResponse() {
    }

    private BaseResponse(String msg, int code) {
        this.message = msg;
        this.code = code;
    }

    private BaseResponse(int code, T data) {
        this.data = data;
        this.code = code;
    }

    public static BaseResponse success() {
        return success;
    }

    public static BaseResponse success(Object data) {
        return new BaseResponse(HttpStatus.HTTP_OK, data);
    }

    public static BaseResponse fail() {
        return fail;
    }

    public static BaseResponse exception() {
        return exception;
    }

    public static BaseResponse exception(int code, String msg) {
        return new BaseResponse(msg, code);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static class Builder<T> {
        private BaseResponse<T> response;

        public Builder() {
            response = new BaseResponse();
        }

        public Builder code(int code) {
            response.setCode(code);
            return this;
        }

        public Builder message(String message) {
            response.setMessage(message);
            return this;
        }

        public Builder data(T data) {
            response.setData(data);
            return this;
        }

        public BaseResponse build() {
            return response;
        }
    }
}
