package com.sparta.cr.carnasagameswebsiteandapi.exceptions.commentexceptions;

public class CommentMustHaveTextException extends RuntimeException {
    public CommentMustHaveTextException(String message) {
        super(message);
    }
}
