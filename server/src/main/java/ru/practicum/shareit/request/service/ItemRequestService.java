package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemInfoDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemInfoDto> getItemRequestsByUser(long userId);

    List<ItemRequestDto> getAllItemRequests(int from, int size);

    ItemRequestWithItemInfoDto getItemRequestById(long id);
}
