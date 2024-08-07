package com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions;

public class InvalidGameException extends RuntimeException {
    public InvalidGameException(String message) {
        super(message);
    }
}
