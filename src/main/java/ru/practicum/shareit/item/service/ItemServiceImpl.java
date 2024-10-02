package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.item.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service

public class ItemServiceImpl implements ItemService {

    private final Map<Long, List<Item>> items;
    private final UserService userService;

    public ItemServiceImpl(UserService userService) {
        this.items = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Item createItem(Long userId, Item item) {
        if (userId == null || item == null) {
            throw new ValidationException("User ID or item cannot be null");
        }
        Long id = setCurrentId();
        item.setId(id);
        item.setOwner(userService.getUser(userId));
        items.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
        return item;
    }

    @Override
    public Item updateItem(User user, Item updatedItem, Long itemId) {
        Long ownerIid = user.getId();
        List<Item> itemList = items.get(ownerIid);
        if (itemList == null || updatedItem  == null) {
            throw new NotFoundException("Item not found");
        }

        Item item = itemList.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst().orElseThrow(() -> new NotFoundException("Item not found"));

        String name = updatedItem.getName();
        String description = updatedItem.getDescription();
        Boolean available = updatedItem.getAvailable();

        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (available != null) {
            item.setAvailable(available);
        }

        return item;
    }

    @Override
    public Item getItem(Long userId, Long itemId) {
        List<Item> itemList = items.get(userId);

        if (itemList == null || itemList.isEmpty()) {
            throw new NotFoundException("Items not found for user with ID: " + userId);
        }

        return itemList.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Item with ID: " + itemId + " not found for user with ID: "
                        + userId));
    }

    @Override
    public List<Item> getItemsByUser(User user) {
        return items.getOrDefault(user.getId(), List.of());
    }

    @Override
    public List<Item> searchItems(Long userId, String text) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> itemList = items.get(userId);
        if (itemList == null || itemList.isEmpty()) {
            throw new NotFoundException("Items not found for user with ID: " + userId);
        }

        String lowerCaseText = text.toLowerCase();
        return itemList.stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable() // Проверяем, что доступность != null и доступна
                        && ((item.getName() != null && item.getName().toLowerCase().contains(lowerCaseText)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerCaseText))))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        items.values().forEach(list -> list.removeIf(item -> item.getId().equals(itemId)));
    }

    private Long setCurrentId() {
        return items.values().stream().mapToLong(List::size).sum() + 1;
    }
}
