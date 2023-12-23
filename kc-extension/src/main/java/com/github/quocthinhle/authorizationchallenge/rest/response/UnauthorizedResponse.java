package com.github.quocthinhle.authorizationchallenge.rest.response;

public class UnauthorizedResponse {

    String error;
    String deviceSession;

    public UnauthorizedResponse(String errorMessage, String deviceSession) {
        this.error = errorMessage;
        this.deviceSession = deviceSession;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDevice() {
        return deviceSession;
    }

    public void setDevice(String device) {
        this.deviceSession = device;
    }
}
