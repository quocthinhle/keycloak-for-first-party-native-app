package com.github.quocthinhle.authorizationchallenge.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {

    @JsonProperty("error")
    String error;
    @JsonProperty("device_session")
    String deviceSession;

    public ErrorResponse() {
        this.error = "unexpected_error";
        this.deviceSession = "";
    }


    public ErrorResponse(String errorMessage) {
        this.error = errorMessage;
        this.deviceSession = "";
    }

    public String getErrorMessage() {
        return error;
    }

    public void setErrorMessage(String errorMessage) {
        this.error = errorMessage;
    }

    public String getDeviceSession() {
        return deviceSession;
    }

    public void setDeviceSession(String deviceSession) {
        this.deviceSession = deviceSession;
    }
}
