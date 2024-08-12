package com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions;

public class ForbiddenRoleException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Access forbidden";
    public ForbiddenRoleException(){
        super(DEFAULT_MESSAGE);
    }
}
