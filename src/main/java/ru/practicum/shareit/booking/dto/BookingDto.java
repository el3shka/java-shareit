package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.service.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private UserDto booker;
    private UserDto owner;
    private BookingStatus status;
}
