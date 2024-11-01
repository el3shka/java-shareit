package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest createItemRequest(ItemRequest request);

    List<ItemRequest> getOthersRequests(long userId);

    List<ItemRequest> getOwnRequests(long userId);

    ItemRequest getItemRequest(long id);

    ItemRequest getRequestById(long requestId, long userId);

    ItemRequest getItemRequestWithRelations(long id, long userId);

}
