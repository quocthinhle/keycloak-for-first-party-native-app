package com.github.quocthinhle.authorizationchallenge.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuccessResponse {

    @JsonProperty("authorization_code")
    private String authorizationCode;

    public SuccessResponse(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }
}
