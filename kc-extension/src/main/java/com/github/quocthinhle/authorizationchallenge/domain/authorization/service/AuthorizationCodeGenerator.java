package com.github.quocthinhle.authorizationchallenge.domain.authorization.service;

import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.DeviceSession;
import org.keycloak.common.util.Time;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.utils.OAuth2Code;
import org.keycloak.protocol.oidc.utils.OAuth2CodeParser;

import java.util.UUID;

public class AuthorizationCodeGenerator {

    public static String gen(KeycloakSession keycloakSession, RealmModel realm, DeviceSession deviceSession, UserModel user) {
        ClientModel clientModel = realm.getClientByClientId(deviceSession.getClientId());
        String remoteAddr = keycloakSession.getContext().getConnection().getRemoteAddr();

        UserSessionModel tokenSession = keycloakSession
                .sessions()
                .createUserSession(
                        realm,
                        user,
                        user.getUsername(),
                        remoteAddr,
                        "impersonate",
                        false,
                        null,
                        null
                );

        AuthenticatedClientSessionModel authSession = keycloakSession
                .sessions()
                .createClientSession(
                        realm,
                        clientModel,
                        tokenSession
                );

        OAuth2Code codeData = new OAuth2Code(
                UUID.randomUUID().toString(),
                Time.currentTime() + realm.getAccessCodeLifespan(),
                UUID.randomUUID().toString(),
                deviceSession.getScope(),
                null,
                deviceSession.getCodeChallenge(),
                deviceSession.getCodeChallengeMethod(),
                tokenSession.getId()
        );

        String authorizationCode = OAuth2CodeParser.persistCode(keycloakSession, authSession, codeData);

        return authorizationCode;
    }

}
