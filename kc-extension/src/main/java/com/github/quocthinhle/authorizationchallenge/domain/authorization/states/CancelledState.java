package com.github.quocthinhle.authorizationchallenge.domain.authorization.states;

import com.github.quocthinhle.authorizationchallenge.exception.InvalidParameterException;
import com.github.quocthinhle.authorizationchallenge.rest.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import org.jose4j.lang.JoseException;

import java.io.IOException;

public class CancelledState extends AuthorizationChallengeState {
    @Override
    public Response handle() throws IOException, JoseException {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("session_cancelled"))
                .build();
    }

    @Override
    public void validateSession() throws InvalidParameterException {}
}
