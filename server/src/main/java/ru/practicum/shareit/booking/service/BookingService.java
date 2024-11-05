package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.NewBookingDtoRequest;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDtoResponse createBooking(NewBookingDtoRequest dto);

    BookingDtoResponse approveBooking(long bookingId, long userId);

    BookingDtoResponse rejectBooking(long bookingId, long userId);

    BookingDtoResponse getBookingByIdOfBookerOrOwner(long bookingId, long userId);

    List<BookingDtoResponse> getAllBookingsByBooker(long userId, BookingState state);

    List<BookingDtoResponse> getAllBookingsByOwner(long userId, BookingState state);
}
