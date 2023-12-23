package com.github.quocthinhle.authorizationchallenge.domain.authorization;

import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.DeviceSession;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.KeycloakSessionWrapper;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.states.*;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.valueobject.PhoneNumber;
import com.github.quocthinhle.authorizationchallenge.exception.DuplicatedException;
import com.github.quocthinhle.authorizationchallenge.exception.InvalidParameterException;
import com.github.quocthinhle.authorizationchallenge.exception.NotFoundException;
import com.github.quocthinhle.authorizationchallenge.utils.DeviceSessionParser;
import jakarta.ws.rs.core.Response;
import org.jose4j.lang.JoseException;
import org.keycloak.keys.DefaultKeyManager;
import org.keycloak.models.KeycloakSession;

import javax.crypto.SecretKey;
import java.io.IOException;

public class AuthorizationChallengeContext {
    private final AuthorizationChallengeState state;

    public AuthorizationChallengeContext(AuthorizationChallengeState state) {
        this.state = state;
    }

    public Response handleContext() throws JoseException, IOException, DuplicatedException, NotFoundException {
        return this.state.handle();
    }

    public static AuthorizationChallengeContext fromState(AuthorizationChallengeState state) {
        return new AuthorizationChallengeContext(state);
    }

    public static Response recoverAndProcessState(
            String phoneNumber,
            String responseType,
            String clientId,
            String codeChallenge,
            String codeChallengeMethod,
            String scope,
            String otp,
            String passcode,
            String deviceSessionCode,
            KeycloakSession keycloakSession
    ) throws JoseException, InvalidParameterException, NotFoundException, IOException, DuplicatedException {
        AuthorizationChallengeState currentState;
        KeycloakSessionWrapper keycloakSessionWrapper = KeycloakSessionWrapper.fromSession(keycloakSession);

        if (deviceSessionCode != null) {
            byte[] deviceSessionData = DeviceSessionParser.parseSession(keycloakSessionWrapper.getActiveAesKey().getSecretKey(), deviceSessionCode);
            DeviceSession deviceSession = DeviceSession.fromBytes(deviceSessionData);

            if (!deviceSession.isStillValid()) {
                currentState = new CancelledState();
                return AuthorizationChallengeContext.fromState(currentState).handleContext();
            }

            deviceSession.setOtp(otp)
                    .setPasscode(passcode);

            switch (deviceSession.getAuthorizationChallengeState()) {
                case WAIT_FOR_OTP: {
                    currentState = new WaitForOTPState(deviceSession, keycloakSessionWrapper);
                    break;
                }
                case WAIT_FOR_NEW_PASSCODE: {
                    currentState = new WaitForNewPasscodeState(deviceSession, keycloakSessionWrapper);
                    break;
                }
                case WAIT_FOR_PASSCODE: {
                    currentState = new WaitForPasscodeState(deviceSession, keycloakSessionWrapper);
                    break;
                }
                default: {
                    currentState = new CancelledState();
                    break;
                }
            }

            return AuthorizationChallengeContext.fromState(currentState).handleContext();
        }

        DeviceSession deviceSession = new DeviceSession();
        deviceSession.setPhoneNumber(new PhoneNumber(phoneNumber))
                .setScope(scope)
                .setClientId(clientId)
                .setResponseType(responseType)
                .setCodeChallenge(codeChallenge)
                .setCodeChallengeMethod(codeChallengeMethod);

        currentState = new InitializeState(deviceSession, keycloakSessionWrapper);

        Response response = new AuthorizationChallengeContext(currentState)
                .handleContext();

        return response;
    }
}
