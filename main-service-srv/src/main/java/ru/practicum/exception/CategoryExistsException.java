package ru.practicum.exception;

public class CategoryExistsException extends RuntimeException {
    public CategoryExistsException(String message) {
        super(message);
    }
}
