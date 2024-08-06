package com.sparta.cr.carnasagameswebsiteandapi.exceptions;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException(String message){
        super(message);
    }
}
