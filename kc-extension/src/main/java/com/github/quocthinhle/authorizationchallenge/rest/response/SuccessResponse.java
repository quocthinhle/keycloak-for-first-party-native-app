package com.github.quocthinhle.authorizationchallenge.rest.response;

public class SuccessResponse {

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
