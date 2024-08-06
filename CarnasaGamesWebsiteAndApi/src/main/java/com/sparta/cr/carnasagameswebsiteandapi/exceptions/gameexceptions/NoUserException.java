package com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions;

public class NoUserException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Unable to create game as no user found!";
    public NoUserException(){
        super(DEFAULT_MESSAGE);
    }
}
