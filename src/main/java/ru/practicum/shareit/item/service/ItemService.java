package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemService {

    Item createItem(Long userId, Item item);

    Item updateItem(User user, Item item, Long itemId);

    Item getItem(Long userId, Long itemId);

    List<Item> getItemsByUser(User user);

    List<Item> searchItems(Long userId, String text);

    void deleteItem(Long itemId);
}
