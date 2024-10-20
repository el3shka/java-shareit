package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {

    Item createItem(Long userId, Item item);

    Item updateItem(User user, Item item, Long itemId);

    Item getItem(Long itemId);

    List<Item> getItemsByUser(User user);

    List<Item> searchItems(String text);

    void deleteItem(Long itemId);

    ItemDetailsDto getItemDetails(Long itemId, Long userId);

    CommentDto addComment(Long itemId, Long userId, String text);

    List<CommentDto> getCommentsForItem(Long itemId);

    boolean isItemAvailable(Long itemId, LocalDateTime start, LocalDateTime end);
}
