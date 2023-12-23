package com.github.quocthinhle.authorizationchallenge.domain.otp.dto;

public class SmsOtp {

    private String phone;
    private String code;

    public SmsOtp(String phone, String code) {
        this.phone = phone;
        this.code = code;
    }

    public SmsOtp(String phone) {
        this.phone = phone;
        this.code = "";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
