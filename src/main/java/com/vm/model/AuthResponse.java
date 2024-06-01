package com.vm.model;

public class AuthResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuthResponse(String message) {
        this.message = message;
    }
}
