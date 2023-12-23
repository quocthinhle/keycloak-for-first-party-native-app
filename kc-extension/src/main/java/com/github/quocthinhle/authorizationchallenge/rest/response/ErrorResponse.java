package com.github.quocthinhle.authorizationchallenge.rest.response;

import jakarta.ws.rs.core.Response;

public class ErrorResponse {

    String errorMessage;
    String deviceSession;

    public ErrorResponse() {
        this.errorMessage = "unexpected_error";
        this.deviceSession = "";
    }


    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
        this.deviceSession = "";
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDeviceSession() {
        return deviceSession;
    }

    public void setDeviceSession(String deviceSession) {
        this.deviceSession = deviceSession;
    }
}
