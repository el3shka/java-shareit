package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    Booking createBooking(Long bookerId, BookingDto booking);

    Booking getBookingById(Long bookingId);

    void deleteBooking(Long bookingId);

    Booking updateBooking(Booking booking, Long bookingId);

    List<Booking> getBookingsByBooker(Long userId, BookingState state);

    List<Booking> getBookingsByOwner(Long ownerId, BookingState state);

    List<Booking> getBookingsByUser(Long userId, Long bookingId, BookingState state);

    List<Booking> getAllBookingsByUser(Long userId, BookingState state);

    Booking approveBooking(Long bookingId, Long ownerId, Boolean approved);

    Optional<Booking> getBookingsByBookingId(Long bookingId, BookingState state);
}
