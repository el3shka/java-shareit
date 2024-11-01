package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class UnsupportedBookingStateFilterException extends RuntimeException {

    private final String invalidValue;

    public UnsupportedBookingStateFilterException(final String invalidValue) {
        super();
        this.invalidValue = invalidValue;
    }
}