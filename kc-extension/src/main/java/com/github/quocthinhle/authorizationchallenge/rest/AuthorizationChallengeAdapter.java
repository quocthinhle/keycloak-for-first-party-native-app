package com.github.quocthinhle.authorizationchallenge.rest;

import com.github.quocthinhle.authorizationchallenge.domain.authorization.AuthorizationChallengeContext;
import com.github.quocthinhle.authorizationchallenge.exception.DuplicatedException;
import com.github.quocthinhle.authorizationchallenge.exception.InvalidParameterException;
import com.github.quocthinhle.authorizationchallenge.exception.NotFoundException;
import com.github.quocthinhle.authorizationchallenge.rest.response.ErrorResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.keycloak.models.KeycloakSession;


public class AuthorizationChallengeAdapter {
    private KeycloakSession keycloakSession;

    public AuthorizationChallengeAdapter(KeycloakSession session) {
        this.keycloakSession = session;
    }


    @POST
    @Path("/authorize")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response handleAuthorizationRequest(
            @FormParam("phone_number") String phoneNumber,
            @FormParam("response_type") String responseType,
            @FormParam("client_id") String clientId,
            @FormParam("code_challenge") String codeChallenge,
            @FormParam("code_challenge_method") String codeChallengeMethod,
            @FormParam("scope") String scope,
            @FormParam("otp") String otp,
            @FormParam("passcode") String passcode,
            @FormParam("device_session") String deviceSessionCode
    ) {
        try {
            return AuthorizationChallengeContext.recoverAndProcessState(
                    phoneNumber,
                    responseType,
                    clientId,
                    codeChallenge,
                    codeChallengeMethod,
                    scope,
                    otp,
                    passcode,
                    deviceSessionCode,
                    this.keycloakSession
            );
        } catch (InvalidParameterException | NotFoundException | DuplicatedException e) {
            return this.respondWithBadRequestStatus(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return this.respondWithBadRequestStatus();
        }
    }

    private Response respondWithBadRequestStatus() {
        return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse()).build();
    }

    private Response respondWithBadRequestStatus(String errorMessage) {
        return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse(errorMessage)).build();
    }
}
