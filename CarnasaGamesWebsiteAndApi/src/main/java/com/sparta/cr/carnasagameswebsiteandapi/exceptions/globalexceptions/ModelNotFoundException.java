package com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions;

public class ModelNotFoundException extends RuntimeException {
    public ModelNotFoundException(String message) {
        super(message);
    }
}
