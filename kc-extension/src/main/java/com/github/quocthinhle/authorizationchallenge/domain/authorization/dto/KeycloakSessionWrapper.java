package com.github.quocthinhle.authorizationchallenge.domain.authorization.dto;

import org.keycloak.keys.DefaultKeyManager;
import org.keycloak.models.KeyManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class KeycloakSessionWrapper {
    KeycloakSession keycloakSession;
    RealmModel realmModel;
    DefaultKeyManager defaultKeyManager;
    KeyManager.ActiveAesKey activeAesKey;

    public static KeycloakSessionWrapper fromSession(KeycloakSession keycloakSession) {
        KeycloakSessionWrapper keycloakSessionData = new KeycloakSessionWrapper();

        keycloakSessionData.setKeycloakSession(keycloakSession);
        keycloakSessionData.setRealmModel(keycloakSession.getContext().getRealm());
        keycloakSessionData.setDefaultKeyManager(new DefaultKeyManager(keycloakSession));
        keycloakSessionData.setActiveAesKey(keycloakSessionData.getDefaultKeyManager().getActiveAesKey(keycloakSessionData.getRealmModel()));

        return keycloakSessionData;
    }

    public DefaultKeyManager getDefaultKeyManager() {
        return defaultKeyManager;
    }

    public void setDefaultKeyManager(DefaultKeyManager defaultKeyManager) {
        this.defaultKeyManager = defaultKeyManager;
    }

    public KeycloakSession getKeycloakSession() {
        return keycloakSession;
    }

    public void setKeycloakSession(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    public RealmModel getRealmModel() {
        return realmModel;
    }

    public void setRealmModel(RealmModel realmModel) {
        this.realmModel = realmModel;
    }

    public KeyManager.ActiveAesKey getActiveAesKey() {
        return activeAesKey;
    }

    public void setActiveAesKey(KeyManager.ActiveAesKey activeAesKey) {
        this.activeAesKey = activeAesKey;
    }
}
