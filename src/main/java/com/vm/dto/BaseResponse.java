package com.vm.dto;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private T data;
    private String message;
    private Integer statusCode;

    public BaseResponse(T data, String message, Integer statusCode) {
        this.data = data;
        this.message = message;
        this.statusCode = statusCode;
    }

    public BaseResponse(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public static <T> BaseResponse<T> success(String message, Integer statusCode) {
        return new BaseResponse<>(message, statusCode);
    }

    public static <T> BaseResponse<T> success(T t, String message, Integer statusCode) {
        return new BaseResponse<>(t, message, statusCode);
    }

    public static <T> BaseResponse<T> error(String message, Integer statusCode) {
        return new BaseResponse<>(message, statusCode);
    }
}
