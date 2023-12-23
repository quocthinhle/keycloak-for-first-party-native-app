package com.github.quocthinhle.authorizationchallenge;

import com.github.quocthinhle.authorizationchallenge.configuration.EnvConfig;
import org.keycloak.Config;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class AuthorizationChallengeEndpointProviderFactory implements RealmResourceProviderFactory {
    private static final String RESOURCE_ID = "first-party-native-app";


    public AuthorizationChallengeEndpointProviderFactory() {
        EnvConfig.configure();
    }

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        // We must create an instance for each session
        return new AuthorizationChallengeEndpointProvider(session);
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return AuthorizationChallengeEndpointProviderFactory.RESOURCE_ID;
    }
}
