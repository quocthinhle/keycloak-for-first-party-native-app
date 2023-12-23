package com.github.quocthinhle.authorizationchallenge.configuration;

import java.util.List;
import java.util.Optional;

public class EnvConfig {
    public static List<String> ALLOWED_CLIENTS;
    public static byte[] ENCRYPTION_KEY;

    public static String OTP_SERVICE_URL;

    public static void configure() {
        ALLOWED_CLIENTS = List.of(Optional.ofNullable(System.getenv("ALLOWED_CLIENTS")).orElse("oidc").split(","));
        ENCRYPTION_KEY = new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        OTP_SERVICE_URL = System.getenv("OTP_SERVICE_URL");
    }



}
