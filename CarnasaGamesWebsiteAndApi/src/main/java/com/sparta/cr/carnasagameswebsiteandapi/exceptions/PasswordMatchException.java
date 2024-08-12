package com.sparta.cr.carnasagameswebsiteandapi.exceptions;

public class PasswordMatchException extends RuntimeException{
    public PasswordMatchException(String message){
        super(message);
    }
}
