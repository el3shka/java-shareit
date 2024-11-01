package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingRetrieveDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemRetrieveDto;
import ru.practicum.shareit.item.dto.ItemUpdatedDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(uses = CommentMapper.class)
public interface ItemMapper {

    @Mapping(target = "comments", expression = "java(new java.util.HashSet<>())")
    @Mapping(target = "request", expression = "java(itemRequestFromItemRequestId(dto))")
    Item mapToItem(ItemCreateDto dto);

    Item mapToItem(ItemUpdatedDto dto);

    @Mapping(target = "requestId", source = "request.id")
    ItemRetrieveDto mapToDto(Item item);

    List<ItemRetrieveDto> mapToDto(List<Item> items);

    @Mapping(target = "bookerId", source = "booker.id")
    ItemBookingRetrieveDto mapToDto(Booking booking);

    default ItemRequest itemRequestFromItemRequestId(final ItemCreateDto dto) {
        if (dto.getRequestId() == null) {
            return null;
        }
        final ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(dto.getRequestId());
        return itemRequest;
    }
}
