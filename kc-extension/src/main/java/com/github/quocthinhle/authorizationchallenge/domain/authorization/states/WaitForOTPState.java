package com.github.quocthinhle.authorizationchallenge.domain.authorization.states;

import com.github.quocthinhle.authorizationchallenge.domain.authorization.AuthorizationState;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.KeycloakSessionWrapper;
import com.github.quocthinhle.authorizationchallenge.domain.otp.dto.SmsOtp;
import com.github.quocthinhle.authorizationchallenge.domain.otp.service.SmsOtpService;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.DeviceSession;
import com.github.quocthinhle.authorizationchallenge.exception.InvalidParameterException;
import com.github.quocthinhle.authorizationchallenge.rest.response.UnauthorizedResponse;
import com.github.quocthinhle.authorizationchallenge.utils.DeviceSessionParser;
import com.github.quocthinhle.authorizationchallenge.utils.Validator;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.SerializationUtils;
import org.jose4j.lang.JoseException;

import java.io.IOException;

public class WaitForOTPState extends AuthorizationChallengeState {

    public WaitForOTPState(DeviceSession deviceSession, KeycloakSessionWrapper keycloakSessionWrapper) throws InvalidParameterException {
        this.deviceSession = deviceSession;
        this.keycloakSessionWrapper = keycloakSessionWrapper;
        this.validateSession();
    }

    @Override
    public Response handle() throws IOException, JoseException {
         boolean isOtpValid = SmsOtpService
                 .isOTPValid(new SmsOtp(this.deviceSession.getPhoneNumber().toRawFormat(), this.deviceSession.getOtp()));

         if (isOtpValid) {
             this.deviceSession
                     .setAuthorizationChallengeState(AuthorizationState.WAIT_FOR_NEW_PASSCODE)
                     .setSignedAt(System.currentTimeMillis());

             String deviceSessionCode = DeviceSessionParser.encryptSession(
                     this.keycloakSessionWrapper.getActiveAesKey().getSecretKey(),
                     SerializationUtils.serialize(this.deviceSession)
             );

             return Response.status(Response.Status.UNAUTHORIZED)
                     .entity(new UnauthorizedResponse("passcode_required", deviceSessionCode))
                     .build();
         }

        // What if someone just use this device_session, it's automatically go to this state, call the validateOTP func
        // Basically we can add timestamp field to the device_session to prevent that, we can expire a session after 1 minute or sth
        if (this.deviceSession.getOtpAttempt() < 3) {
            this.deviceSession
                    .setOtpAttempt(this.deviceSession.getOtpAttempt() + 1)
                    .setSignedAt(System.currentTimeMillis());

            String deviceSessionCode = DeviceSessionParser.encryptSession(
                    this.keycloakSessionWrapper.getActiveAesKey().getSecretKey(),
                    SerializationUtils.serialize(this.deviceSession)
            );

            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new UnauthorizedResponse("otp_failed", deviceSessionCode))
                    .build();
        }


        // Reached limit
        this.deviceSession
                .setAuthorizationChallengeState(AuthorizationState.CANCELLED)
                .setSignedAt(System.currentTimeMillis());

        String deviceSessionCode = DeviceSessionParser.encryptSession(
                this.keycloakSessionWrapper.getActiveAesKey().getSecretKey(),
                SerializationUtils.serialize(this.deviceSession)
        );

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new UnauthorizedResponse("session_cancelled", deviceSessionCode))
                .build();
    }

    @Override
    public void validateSession() throws InvalidParameterException {
        String otp = this.deviceSession.getOtp();
        String phoneNumber = this.deviceSession.getPhoneNumber().toRawFormat();
        String codeChallenge = this.deviceSession.getCodeChallenge();
        String codeChallengeMethod = this.deviceSession.getCodeChallengeMethod();
        String responseType = this.deviceSession.getResponseType();
        String scope = this.deviceSession.getScope();

        boolean isSessionValid = Validator.isVietnamesePhoneNumber(phoneNumber)
                && Validator.isNotEmpty(codeChallenge)
                && Validator.isNotEmpty(codeChallengeMethod)
                && Validator.isNotEmpty(responseType)
                && Validator.isNotEmpty(scope)
                && Validator.isNotEmpty(otp)
                && responseType.equals("code");

        if (!isSessionValid) {
            throw new InvalidParameterException();
        }
    }
}
