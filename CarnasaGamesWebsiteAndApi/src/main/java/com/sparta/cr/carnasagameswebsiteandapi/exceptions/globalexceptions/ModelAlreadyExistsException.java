package com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions;

public class ModelAlreadyExistsException extends RuntimeException {
    public ModelAlreadyExistsException(String message) {
        super(message);
    }
}
