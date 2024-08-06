package com.sparta.cr.carnasagameswebsiteandapi.exceptions;

public class InvalidEmailException extends RuntimeException{
    public InvalidEmailException(String message){
        super("Email: " + message + " is not valid, please enter a valid email in the format this.isvalid@tryagain.com");
    }
}
