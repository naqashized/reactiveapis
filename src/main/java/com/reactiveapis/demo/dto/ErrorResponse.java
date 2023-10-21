package com.reactiveapis.demo.dto;

public class ErrorResponse {
    private String message;

    public ErrorResponse(){

    }
    public ErrorResponse(String message){
        this.message = message;
    }

    public static ErrorResponse create(Exception exception){
        return new ErrorResponse(exception.getMessage());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
