package com.github.quocthinhle.authorizationchallenge.domain.authorization.states;

import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.KeycloakSessionWrapper;
import com.github.quocthinhle.authorizationchallenge.exception.DuplicatedException;
import com.github.quocthinhle.authorizationchallenge.exception.InvalidParameterException;
import com.github.quocthinhle.authorizationchallenge.domain.authorization.dto.DeviceSession;
import com.github.quocthinhle.authorizationchallenge.exception.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.jose4j.lang.JoseException;
import org.keycloak.models.KeycloakSession;

import java.io.IOException;

public abstract class AuthorizationChallengeState {

    DeviceSession deviceSession;
    KeycloakSessionWrapper keycloakSessionWrapper;

    public abstract Response handle() throws IOException, JoseException, DuplicatedException, NotFoundException;
    public abstract void validateSession() throws InvalidParameterException;
}
