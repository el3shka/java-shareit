package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.*;
import ru.practicum.shareit.item.model.*;

import java.util.List;

@Mapper
public interface ItemRequestMapper {

    @Mapping(target = "requester.id", source = "userId")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "items", expression = "java(new java.util.HashSet<>())")
    ItemRequest mapToItemRequest(Long userId, ItemRequestCreateDto dot);

    @Mapping(target = "items", source = "items")
    ItemRequestRetrieveDto mapToDto(ItemRequest itemRequest);

    List<ItemRequestRetrieveDto> mapToDto(List<ItemRequest> itemRequests);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "description", source = "item.description")
    ItemRequestItemRetrieveDto mapToDto(Item item);
}
