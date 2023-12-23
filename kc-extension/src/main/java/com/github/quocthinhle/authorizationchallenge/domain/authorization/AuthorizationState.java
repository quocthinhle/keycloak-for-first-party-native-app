package com.github.quocthinhle.authorizationchallenge.domain.authorization;

public enum AuthorizationState {
    INITIAL,
    WAIT_FOR_OTP,
    WAIT_FOR_NEW_PASSCODE,
    WAIT_FOR_PASSCODE,
    CANCELLED,
}
