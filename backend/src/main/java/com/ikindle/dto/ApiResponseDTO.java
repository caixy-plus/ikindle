package com.ikindle.dto;

import lombok.Data;

@Data
public class ApiResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
    private long timestamp;
    private String path;

    public ApiResponseDTO() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ApiResponseDTO<T> success(T data) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> ApiResponseDTO<T> success(T data, String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = true;
        response.data = data;
        response.message = message;
        return response;
    }

    public static <T> ApiResponseDTO<T> error(String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = false;
        response.message = message;
        return response;
    }

    public static <T> ApiResponseDTO<T> error(String message, T data) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = false;
        response.message = message;
        response.data = data;
        return response;
    }
}