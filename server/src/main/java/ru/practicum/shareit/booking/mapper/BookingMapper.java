package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingRetrieveDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "item.id", source = "dto.itemId")
    @Mapping(target = "booker.id", source = "userId")
    @Mapping(target = "status", expression = "java(BookingStatus.WAITING)")
    Booking mapToBooking(BookingCreateDto dto, Long userId);

    BookingRetrieveDto mapToDto(Booking booking);

    List<BookingRetrieveDto> mapToDto(List<Booking> bookings);
}
