package com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions;

public class InvalidRoleException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Invalid role provided, roles can either be ROLE_ADMIN or ROLE_USER";
    public InvalidRoleException(){
        super(DEFAULT_MESSAGE);
    }
}
