package com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions;

public class GameAlreadyExistsException extends RuntimeException {
    public GameAlreadyExistsException(String message) {
        super("Game ID: " + message + " already exists, please choose a different game ID");
    }
}
