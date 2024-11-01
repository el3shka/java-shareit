package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Item item, long userId);

    Item getItem(long id, long userId);

    List<Item> getItems(long userId);

    Item updateItem(long userId, Item updatedItemDto, long itemId);

    List<Item> getItemsByUser(Long userId);

    List<Item> searchItems(String text, long userId);

    void deleteItem(long id, long userId);

    Item getItemToBook(long id, long userId);

    boolean existByOwnerId(long userId);
}
