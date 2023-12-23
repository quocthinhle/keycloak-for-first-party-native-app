package com.github.quocthinhle.authorizationchallenge.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static boolean isVietnamesePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }

        if (phoneNumber.length() < 10) {
            return false;
        }

        String regex = "^(0|\\+84|84)([1-9]\\d{8}|[1-9]\\d{9})$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);

        return matcher.matches();
    }

    public static boolean isNotEmpty(String o) {
        return o != null && !o.equals("");
    }
}
