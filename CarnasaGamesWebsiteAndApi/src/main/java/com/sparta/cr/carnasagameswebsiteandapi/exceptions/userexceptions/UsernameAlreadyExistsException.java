package com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super("Username: " + message + " already in use.");
    }
}
