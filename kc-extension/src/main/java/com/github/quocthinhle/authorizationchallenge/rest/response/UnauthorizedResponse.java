package com.github.quocthinhle.authorizationchallenge.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UnauthorizedResponse {
    @JsonProperty("error")
    String error;
    @JsonProperty("device_session")
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

    public String getDeviceSession() {
        return deviceSession;
    }

    public void setDeviceSession(String device) {
        this.deviceSession = device;
    }
}

