package com.github.quocthinhle.authorizationchallenge.domain.authorization.states;

import com.github.quocthinhle.authorizationchallenge.configuration.EnvConfig;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.AuthorizationState;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.KeycloakSessionWrapper;
import com.github.quocthinhle.authorizationchallenge.exception.InvalidParameterException;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.DeviceSession;
import com.github.quocthinhle.authorizationchallenge.rest.response.UnauthorizedResponse;
import com.github.quocthinhle.authorizationchallenge.utils.DeviceSessionParser;
import com.github.quocthinhle.authorizationchallenge.utils.Validator;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.SerializationUtils;
import org.jose4j.lang.JoseException;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.io.IOException;

public class InitializeState extends AuthorizationChallengeState {
    public InitializeState(DeviceSession deviceSession, KeycloakSessionWrapper keycloakSessionWrapper) throws InvalidParameterException {
        this.deviceSession = deviceSession;
        this.keycloakSessionWrapper = keycloakSessionWrapper;
        this.validateSession();
    }

    @Override
    public Response handle() throws JoseException, IOException {
        String requestedPhoneNumber = this.deviceSession.getPhoneNumber().toRawFormat();
        RealmModel realmModel = this.keycloakSessionWrapper.getKeycloakSession().getContext().getRealm();
        UserModel user = this.keycloakSessionWrapper.getKeycloakSession()
                .users()
                .getUserByUsername(realmModel, requestedPhoneNumber);


        if (user == null) {
            // SmsOtpService.sendOTP(new SmsOtp(this.deviceSession.getPhoneNumber()));
            this.deviceSession
                    .setAuthorizationChallengeState(AuthorizationState.WAIT_FOR_OTP)
                    .setSignedAt(System.currentTimeMillis());

            String deviceSessionCode = DeviceSessionParser.encryptSession(
                    this.keycloakSessionWrapper.getActiveAesKey().getSecretKey(),
                    SerializationUtils.serialize(this.deviceSession)
            );

            return Response.status(401)
                    .entity(new UnauthorizedResponse("otp_required", deviceSessionCode))
                    .build();
        }

        this.deviceSession
                .setAuthorizationChallengeState(AuthorizationState.WAIT_FOR_PASSCODE)
                .setSignedAt(System.currentTimeMillis());

        String deviceSessionCode = DeviceSessionParser.encryptSession(
                this.keycloakSessionWrapper.getActiveAesKey().getSecretKey(),
                SerializationUtils.serialize(this.deviceSession)
        );

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new UnauthorizedResponse("passcode_required", deviceSessionCode))
                .build();
    }

    @Override
    public void validateSession() throws InvalidParameterException {
        String clientId = this.deviceSession.getClientId();
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
                && responseType.equals("code")
                && EnvConfig.ALLOWED_CLIENTS.contains(clientId);

        if (!isSessionValid) {
            throw new InvalidParameterException();
        }
    }
}
