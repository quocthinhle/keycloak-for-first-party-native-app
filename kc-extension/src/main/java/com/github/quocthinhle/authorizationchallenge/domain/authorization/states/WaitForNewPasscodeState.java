package com.github.quocthinhle.authorizationchallenge.domain.authorization.states;

import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.DeviceSession;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.KeycloakSessionWrapper;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.service.AuthorizationCodeGenerator;
import com.github.quocthinhle.authorizationchallenge.exception.DuplicatedException;
import com.github.quocthinhle.authorizationchallenge.exception.InvalidParameterException;
import com.github.quocthinhle.authorizationchallenge.rest.response.SuccessResponse;
import com.github.quocthinhle.authorizationchallenge.utils.Validator;
import jakarta.ws.rs.core.Response;
import org.keycloak.credential.PasswordCredentialProvider;
import org.keycloak.models.*;

public class WaitForNewPasscodeState extends AuthorizationChallengeState {
    public WaitForNewPasscodeState(
            DeviceSession deviceSession,
            KeycloakSessionWrapper keycloakSessionWrapper
    ) throws InvalidParameterException {
        this.deviceSession = deviceSession;
        this.keycloakSessionWrapper = keycloakSessionWrapper;
        this.validateSession();
    }

    @Override
    public Response handle() throws DuplicatedException {
        RealmModel realmModel = this.keycloakSessionWrapper.getRealmModel();

        UserModel user = this.keycloakSessionWrapper.getKeycloakSession().users().getUserByUsername(realmModel,
                this.deviceSession.getPhoneNumber().toRawFormat());

        if (user != null) {
            throw new DuplicatedException();
        }

        user = this.createUser();
        String authorizationCode = AuthorizationCodeGenerator.gen(
                this.keycloakSessionWrapper.getKeycloakSession(),
                realmModel,
                this.deviceSession,
                user
        );

        return Response.status(Response.Status.OK)
                .entity(new SuccessResponse(authorizationCode))
                .build();
    }

    private UserModel createUser() {
        RealmModel realmModel = this.keycloakSessionWrapper.getRealmModel();
        KeycloakSession session = this.keycloakSessionWrapper.getKeycloakSession();
        UserModel user = this.keycloakSessionWrapper
                .getKeycloakSession()
                .users()
                .addUser(realmModel, this.deviceSession.getPhoneNumber().toRawFormat());

        PasswordCredentialProvider passwordCredentialProvider = new PasswordCredentialProvider(session);
        passwordCredentialProvider.createCredential(realmModel, user, this.deviceSession.getPasscode());

        user.setEnabled(true);

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
