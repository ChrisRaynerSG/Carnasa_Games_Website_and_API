package com.sparta.cr.carnasagameswebsiteandapi.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super("Email: " + message + " already in use.");
    }
}
