package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequest toItemRequest(final ItemRequestDto requestDto, User user) {
        if (requestDto == null) return null;
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requester(user)
                .build();
    }

    public ItemRequestDto toItemRequestDto(final ItemRequest itemRequest) {
        if (itemRequest == null) return null;
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .userId(itemRequest.getRequester().getId())
                .created(itemRequest.getCreated())
                .build();
    }

    public List<ItemRequestDto> toItemRequestDto(final List<ItemRequest> itemRequest) {
        return itemRequest.stream().map(this::toItemRequestDto).toList();
    }

    public ItemRequestWithItemInfoDto toItemRequestWithItemInfoDto(final ItemRequest itemRequest) {
        if (itemRequest == null) return null;
        List<ItemRequestWithItemInfoDto.ItemShortDto> shortItemsDto = new ArrayList<>();
        if (!itemRequest.getItems().isEmpty()) {
            shortItemsDto = itemRequest.getItems().stream()
                    .map(item -> new ItemRequestWithItemInfoDto.ItemShortDto(item.getId(),
                            item.getName(),
                            item.getOwner().getId())).toList();
        }
        return ItemRequestWithItemInfoDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(shortItemsDto)
                .build();
    }

    public List<ItemRequestWithItemInfoDto> toItemRequestWithItemInfoDto(final List<ItemRequest> itemRequest) {
        return itemRequest.stream().map(this::toItemRequestWithItemInfoDto).toList();
    }
}
