package com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions;

public class InvalidUserException extends RuntimeException{

    public InvalidUserException(String message){
        super(message);
    }
}
