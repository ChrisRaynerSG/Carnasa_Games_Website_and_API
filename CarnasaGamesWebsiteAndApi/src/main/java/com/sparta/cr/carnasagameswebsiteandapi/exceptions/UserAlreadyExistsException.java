package com.sparta.cr.carnasagameswebsiteandapi.exceptions;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String message){
        super("User with ID: " + message + " already in use.");
    }
}
