package com.github.quocthinhle.authorizationchallenge.domain.authorization.states;

import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.DeviceSession;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.KeycloakSessionWrapper;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.service.AuthorizationCodeGenerator;
import com.github.quocthinhle.authorizationchallenge.exception.InvalidParameterException;
import com.github.quocthinhle.authorizationchallenge.exception.NotFoundException;
import com.github.quocthinhle.authorizationchallenge.rest.response.ErrorResponse;
import com.github.quocthinhle.authorizationchallenge.rest.response.SuccessResponse;
import com.github.quocthinhle.authorizationchallenge.utils.Validator;
import jakarta.ws.rs.core.Response;
import org.keycloak.models.*;

public class WaitForPasscodeState extends AuthorizationChallengeState {
    public WaitForPasscodeState(DeviceSession deviceSession, KeycloakSessionWrapper keycloakSessionWrapper) throws InvalidParameterException {
        this.keycloakSessionWrapper = keycloakSessionWrapper;
        this.deviceSession = deviceSession;
        this.validateSession();
    }

    @Override
    public Response handle() throws NotFoundException {
        RealmModel realm = this.keycloakSessionWrapper.getRealmModel();
        String passcode = this.deviceSession.getPasscode();

        UserModel user = this.getUser(realm);

        if (user == null) {
            throw new NotFoundException("user");
        }

        boolean isPasscodeValid = user.credentialManager().isValid(UserCredentialModel.password(passcode));

        System.out.println("Is valid: " + isPasscodeValid);

        if (!isPasscodeValid) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("invalid_credentials"))
                    .build();
        }

        String authorizationCode = AuthorizationCodeGenerator.gen(
                this.keycloakSessionWrapper.getKeycloakSession(),
                realm,
                this.deviceSession,
                user
        );


        return Response.status(Response.Status.OK)
                .entity(new SuccessResponse(authorizationCode))
                .build();
    }

    private UserModel getUser(RealmModel realm) {
        UserModel user = this.keycloakSessionWrapper
                .getKeycloakSession()
                .users()
                .getUserByUsername(realm, this.deviceSession.getPhoneNumber().toRawFormat());

        return user;
    }

    @Override
    public void validateSession() throws InvalidParameterException {
        String phoneNumber = this.deviceSession.getPhoneNumber().toRawFormat();
        String codeChallenge = this.deviceSession.getCodeChallenge();
        String codeChallengeMethod = this.deviceSession.getCodeChallengeMethod();
        String responseType = this.deviceSession.getResponseType();
        String scope = this.deviceSession.getScope();
        String passcode = this.deviceSession.getPasscode();

        boolean isSessionValid = Validator.isVietnamesePhoneNumber(phoneNumber)
                && Validator.isNotEmpty(codeChallenge)
                && Validator.isNotEmpty(codeChallengeMethod)
                && Validator.isNotEmpty(responseType)
                && Validator.isNotEmpty(scope)
                && Validator.isNotEmpty(passcode)
                && responseType.equals("code");

        if (!isSessionValid) {
            throw new InvalidParameterException();
        }
    }
}
