package com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions;

public class InvalidTitleException extends RuntimeException {
    public InvalidTitleException(String message) {
        super("Title: " + message + " is not valid, titles can only contain letters, numbers, and spaces.");
    }
}
