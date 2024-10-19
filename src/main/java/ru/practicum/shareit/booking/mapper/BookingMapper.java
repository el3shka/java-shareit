package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGivenDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto toBookingDto(Booking booking);

    @Mapping(source = "itemId", target = "item", qualifiedByName = "mapItemIdToItem")
    Booking toBooking(BookingDto bookingDto, @Context ItemService itemService);

    List<BookingDto> toBookingDtoList(List<Booking> bookingList);

    List<BookingGivenDto> toBookingGivenDtoList(List<Booking> bookingList);

    BookingGivenDto toBookingGivenDto(Booking booking);

    @Named("mapItemIdToItem")
    default Item mapItemIdToItem(Long itemId, @Context ItemService itemService) {
        Item searchedItem = itemService.getItem(itemId);
        Item item = new Item();
        item.setId(searchedItem.getId());
        item.setName(searchedItem.getName());
        item.setDescription(searchedItem.getDescription());
        item.setOwner(searchedItem.getOwner());
        return item;
    }
}
