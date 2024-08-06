package com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String message) {
        super("User with name: " + message + " was not found.");
    }
}
