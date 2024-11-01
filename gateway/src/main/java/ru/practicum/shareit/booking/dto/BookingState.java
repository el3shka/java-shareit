package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {
    ALL,       // Все бронирования
    CURRENT,   // Текущие бронирования
    PAST,      // Завершённые бронирования
    FUTURE,    // Будущие бронирования
    WAITING,   // Ожидающие подтверждения
    REJECTED;  // Отклонённые бронирования

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
