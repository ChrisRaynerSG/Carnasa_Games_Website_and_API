package com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions;

public class CantChangeUsernameException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Username cannot be changed!";
    public CantChangeUsernameException() {
        super(DEFAULT_MESSAGE);
    }
}
