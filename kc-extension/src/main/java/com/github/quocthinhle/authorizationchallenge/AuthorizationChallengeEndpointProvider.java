package com.github.quocthinhle.authorizationchallenge;

import com.github.quocthinhle.authorizationchallenge.rest.AuthorizationChallengeAdapter;
import org.keycloak.models.KeycloakSession;

import org.keycloak.services.resource.RealmResourceProvider;


public class AuthorizationChallengeEndpointProvider implements RealmResourceProvider {
    private KeycloakSession keycloakSession;


    public AuthorizationChallengeEndpointProvider(KeycloakSession session) {
        this.keycloakSession = session;
    }


    @Override
    public Object getResource() {
        return new AuthorizationChallengeAdapter(this.keycloakSession);
    }

    @Override
    public void close() {

    }


}
