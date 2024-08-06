package com.sparta.cr.carnasagameswebsiteandapi.exceptions.commentexceptions;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
