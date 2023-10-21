package ru.practicum.exception;

public class CommentValidationException extends RuntimeException {
    public CommentValidationException(String message) {
        super(message);
    }
}
