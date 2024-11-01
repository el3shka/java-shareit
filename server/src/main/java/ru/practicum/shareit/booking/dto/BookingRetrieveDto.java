package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "id")
public class BookingRetrieveDto {

    private Long id;
    private BookingItemRetrieveDto item;
    private BookingBookerRetrieveDto booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
}