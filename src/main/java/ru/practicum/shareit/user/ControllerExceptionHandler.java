package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.shareit.user.exception.ConflictException;
import ru.practicum.shareit.user.exception.NotFoundException;
import ru.practicum.shareit.user.exception.ValidationException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    protected ProblemDetail handleNotFoundException(NotFoundException exception) {
        log.warn(exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler
    protected ProblemDetail handleValidationException(ValidationException exception) {
        log.warn(exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler
    protected ProblemDetail handleConflictException(ConflictException exception) {
        log.warn(exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
            @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn(exception.getMessage());
        HttpStatus statusCode = HttpStatus.BAD_REQUEST;
        String detail = exception.getBindingResult().getFieldErrors().stream()
                .map(e -> "'%s' %s".formatted(e.getField(), e.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        return handleExceptionInternal(exception, ProblemDetail.forStatusAndDetail(statusCode, detail), headers,
                statusCode, request);
    }

    @ExceptionHandler
    protected ProblemDetail handleThrowable(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Please contact site admin");
    }
}
