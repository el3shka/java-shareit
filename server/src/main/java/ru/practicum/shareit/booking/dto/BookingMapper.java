package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

@Component
public class BookingMapper {

    public Booking toNewBooking(NewBookingDtoRequest dto, Item item, User booker) {
        if (dto == null) return null;
        return Booking.builder()
                .item(item)
                .booker(booker)
                .startTime(dto.getStart())
                .endTime(dto.getEnd())
                .status(Status.WAITING)
                .build();
    }

    public BookingDtoResponse toBookingDtoResponse(Booking booking) {
        if (booking == null) return null;
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .item(BookingDtoResponse.ItemDtoResponse.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .booker(new BookingDtoResponse.UserDtoResponse(booking.getBooker().getId()))
                .start(booking.getStartTime())
                .end(booking.getEndTime())
                .status(booking.getStatus().toString())
                .build();
    }

    public List<BookingDtoResponse> toBookingDtoResponse(List<Booking> bookings) {
        return bookings.stream().map(this::toBookingDtoResponse).toList();
    }
}
