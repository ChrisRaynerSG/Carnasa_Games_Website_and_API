package com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super("User with ID: " + message + " not found.");
    }
}
