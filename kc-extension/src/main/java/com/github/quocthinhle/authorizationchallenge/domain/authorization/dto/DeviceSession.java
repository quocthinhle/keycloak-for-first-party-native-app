package com.github.quocthinhle.authorizationchallenge.domain.authorization.dto;

import com.github.quocthinhle.authorizationchallenge.domain.authorization.AuthorizationState;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.valueobject.PhoneNumber;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * Must be a doc for DeviceSession
 */
public class DeviceSession implements Serializable {

    private PhoneNumber phoneNumber;
    private String passcode;
    private String otp;
    private AuthorizationState authorizationChallengeState;
    private String clientId;
    private String scope;
    private String responseType;
    private String codeChallenge;
    private String codeChallengeMethod;

    private long signedAt;

    public static DeviceSession fromBytes(byte[] raw) {
        return SerializationUtils.deserialize(raw);
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public String getPasscode() {
        return passcode;
    }

    public String getOtp() {
        return otp;
    }

    public AuthorizationState getAuthorizationChallengeState() {
        return authorizationChallengeState;
    }

    public String getClientId() {
        return clientId;
    }

    public String getScope() {
        return scope;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getCodeChallenge() {
        return codeChallenge;
    }

    public String getCodeChallengeMethod() {
        return codeChallengeMethod;
    }

    public int otpAttempt = 0;

    public DeviceSession setAuthorizationChallengeState(AuthorizationState authorizationChallengeState) {
        this.authorizationChallengeState = authorizationChallengeState;
        return this;
    }

    public int getOtpAttempt() {
        return otpAttempt;
    }

    public DeviceSession setOtpAttempt(int otpAttempt) {
        this.otpAttempt = otpAttempt;
        return this;
    }

    public long getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(long signedAt) {
        this.signedAt = signedAt;
    }

    public DeviceSession setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public DeviceSession setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public DeviceSession setResponseType(String responseType) {
        this.responseType = responseType;
        return this;
    }

    public DeviceSession setCodeChallenge(String codeChallenge) {
        this.codeChallenge = codeChallenge;
        return this;
    }

    public DeviceSession setCodeChallengeMethod(String codeChallengeMethod) {
        this.codeChallengeMethod = codeChallengeMethod;
        return this;
    }

    public DeviceSession setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public DeviceSession setOtp(String otp) {
        this.otp = otp;
        return this;
    }

    public DeviceSession setPasscode(String passcode) {
        this.passcode = passcode;
        return this;
    }

    public boolean isStillValid() {
        long current = System.currentTimeMillis();
        return current - this.getSignedAt() <= 5 * 60 * 1000;
    }

    @Override
    public String toString() {
        return "DeviceSession{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", passcode='" + passcode + '\'' +
                ", otp='" + otp + '\'' +
                ", authorizationChallengeState='" + authorizationChallengeState + '\'' +
                ", clientId='" + clientId + '\'' +
                ", scope='" + scope + '\'' +
                ", responseType='" + responseType + '\'' +
                ", codeChallenge='" + codeChallenge + '\'' +
                ", codeChallengeMethod='" + codeChallengeMethod + '\'' +
                '}';
    }
}
