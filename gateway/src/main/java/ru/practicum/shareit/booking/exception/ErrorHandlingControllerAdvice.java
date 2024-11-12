package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandlingControllerAdvice {
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(constraintViolation -> {
                    log.warn("{} - {}", constraintViolation.getPropertyPath(), constraintViolation.getMessage());
                    return new ErrorResponse(String.format("Invalid value of the %s parameter: %s",
                            constraintViolation.getPropertyPath().toString(),
                            constraintViolation.getMessage()));
                })
                .findFirst().orElse(new ErrorResponse("server error"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ErrorResponse> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors().stream()
                .map(error -> new ErrorResponse(error.getDefaultMessage())).toList();

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final Exception ex) {
        log.error(ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

}
