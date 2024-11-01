package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Ошибка валидации данных: {}.", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("type", "about:blank");
        response.put("title", "Bad Request");
        response.put("status", 400);
        response.put("detail", "Check that data you sent is correct");
        response.put("instance", "/items");

        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage()
                ));
        response.put("error", fieldErrors);

        return response;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationException(ValidationException ex) {
        log.error("Ошибка валидации данных: {}.", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("type", "about:blank");
        response.put("title", "Bad Request");
        response.put("status", 400);
        response.put("detail", "Check that data you sent is correct");
        response.put("instance", "/items");

        response.put("error", Map.of("available", "не должно равняться null")); // Можно адаптировать под реальный кейс

        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleThrowable(final Throwable e) {
        log.error("Возникла ошибка: {}.", e.getMessage(), e);

        // Создаем ответ для внутренних ошибок
        return Map.of(
                "type", "about:blank",
                "title", "Internal Server Error",
                "status", 500,
                "detail", "An unexpected error occurred",
                "instance", "/items",
                "error", Map.of("message", e.getMessage())
        );
    }
}