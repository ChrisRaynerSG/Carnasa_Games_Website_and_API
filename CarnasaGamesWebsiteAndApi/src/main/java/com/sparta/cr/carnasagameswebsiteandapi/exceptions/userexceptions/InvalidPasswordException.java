package com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions;

public class InvalidPasswordException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Invalid password provided. Passwords must contain at least one number, special character, lowercase and uppercase letter, and be greater than 8 characters.";
    public InvalidPasswordException(){
        super(DEFAULT_MESSAGE);
    }
}
