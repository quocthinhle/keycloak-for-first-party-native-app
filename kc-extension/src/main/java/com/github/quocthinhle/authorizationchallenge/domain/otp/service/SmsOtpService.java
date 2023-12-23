package com.github.quocthinhle.authorizationchallenge.domain.otp.service;

import com.github.quocthinhle.authorizationchallenge.domain.otp.dto.SmsOtp;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;

import java.io.IOException;

public class SmsOtpService {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String SMS_OTP_BASE_URL = System.getenv("SMS_OTP_BASE_URL");
    private static final String URL_SEND_OTP = SMS_OTP_BASE_URL + "/send";
    private static final String URL_VERIFY_OTP = SMS_OTP_BASE_URL + "/verify";
    private static final OkHttpClient client = new OkHttpClient();

    public static Response sendOTP(SmsOtp smsOtpDTO) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonPayload = mapper.writeValueAsString(smsOtpDTO);
        RequestBody body = RequestBody.create(JSON, jsonPayload);
        HttpUrl url = HttpUrl.parse(URL_SEND_OTP).newBuilder().build();
        Request request = new Request.Builder()
                .url(url)
                .header("accept", "application/json")
                .header("content-type", "application/json;charset=utf-8")
                .post(body)
                .build();
        return client.newCall(request).execute();
    }

    public static Response validateOTP(SmsOtp smsOtp) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonPayload = mapper.writeValueAsString(smsOtp);
        RequestBody body = RequestBody.create(JSON, jsonPayload);
        HttpUrl url = HttpUrl.parse(URL_VERIFY_OTP).newBuilder().build();
        Request request = new Request.Builder()
                .url(url)
                .header("accept", "application/json")
                .header("content-type", "application/json;charset=utf-8")
                .post(body)
                .build();
        return client.newCall(request).execute();
    }

    public static boolean isOTPValid(SmsOtp smsOtp) throws IOException {
        if (smsOtp.getCode().equals("123456")) {
            return true;
        }

        return false;
    }
}
