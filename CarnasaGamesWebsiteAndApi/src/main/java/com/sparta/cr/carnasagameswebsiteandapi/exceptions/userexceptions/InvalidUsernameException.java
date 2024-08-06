package com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions;

public class InvalidUsernameException extends RuntimeException{
    public InvalidUsernameException(String message){
        super("Username: " + message + " is not a valid username, usernames must contain no spaces, consist of letters, numbers or _ and - and be between 3 and 20 characters in length.");
    }
}
