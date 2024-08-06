package com.sparta.cr.carnasagameswebsiteandapi.exceptions.gameexceptions;

public class InvalidGenreException extends RuntimeException{
    public InvalidGenreException(String message){
        super("Genre: " + message + " is not a valid genre, please select from:\n {Puzzle,Platformer,Shooter,Racing,Fighting,Sports,Adventure,Strategy,Simulation,Arcade}");
    }
}
