package com.github.quocthinhle.authorizationchallenge.exception;

public class NotFoundException extends Exception {

    public NotFoundException(String entity) {
        super(entity + " not found");
    }

    public NotFoundException() {
        super();
    }

}
