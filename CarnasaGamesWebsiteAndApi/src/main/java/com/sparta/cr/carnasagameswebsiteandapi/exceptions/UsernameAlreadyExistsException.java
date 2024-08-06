package com.sparta.cr.carnasagameswebsiteandapi.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super("Username: " + message + " already in use.");
    }
}
