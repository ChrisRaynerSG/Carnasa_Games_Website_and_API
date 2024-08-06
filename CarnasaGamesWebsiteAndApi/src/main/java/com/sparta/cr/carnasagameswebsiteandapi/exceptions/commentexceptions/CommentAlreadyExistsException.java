package com.sparta.cr.carnasagameswebsiteandapi.exceptions.commentexceptions;

public class CommentAlreadyExistsException extends RuntimeException {
    public CommentAlreadyExistsException(String message) {
        super(message);
    }
}
