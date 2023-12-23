package com.github.quocthinhle.authorizationchallenge.domain.authorization.valueobject;

import com.github.quocthinhle.authorizationchallenge.exception.InvalidParameterException;
import com.github.quocthinhle.authorizationchallenge.utils.Validator;

import java.io.Serializable;

public class PhoneNumber implements Serializable {

    private String raw;

    public PhoneNumber(String raw) throws InvalidParameterException {
        if (!Validator.isVietnamesePhoneNumber(raw)) {
            throw new InvalidParameterException();
        }

        this.raw = formatPhoneNumber(raw);
    }

    public String toRawFormat() {
        return this.raw;
    }

    private static String formatPhoneNumber(String phoneNumber) {
        String formattedNumber = phoneNumber.replaceFirst("^(\\+84|84)\\s?", "0");
        return formattedNumber;
    }

    @Override
    public boolean equals(Object obj) {
        PhoneNumber another = (PhoneNumber) obj;
        return this.raw.equals(another.toRawFormat());
    }
}
