package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(Booking booking);

    Booking getBookingById(long bookingId, long userId);

    void deleteBooking(long bookingId);

    Booking updateBooking(Booking booking, long bookingId);

    List<Booking> getBookingsByBooker(long userId, BookingState state);

    List<Booking> getBookingsByOwner(long ownerId, BookingState state);

    Booking approveBooking(long bookingId, long ownerId, boolean approved);

    Booking changeStatus(long bookingId, long ownerId, Boolean approved);

    List<Booking> findAllCompleteBookingByUserIdAndItemId(long userId, long id);
}
