package com.sparta.cr.carnasagameswebsiteandapi.exceptions.scoreexceptions;

public class ScoreOutOfBoundsException extends RuntimeException {
    public ScoreOutOfBoundsException(String message) {
        super("Unable to save score as " + message + " is out of bounds");
    }
}
