package ru.practicum.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.*;
import ru.practicum.exception.model.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({EmailAlreadyExistsException.class, CategoryExistsException.class,
            CategoryInUseException.class, RequestValidationException.class, EventValidationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({EventDateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}
