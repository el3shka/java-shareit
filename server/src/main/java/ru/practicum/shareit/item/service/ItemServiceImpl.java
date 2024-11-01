package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.*;


import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemRequestService itemRequestService;

    @Override
    public Item createItem(Item item, long userId) {
        Objects.requireNonNull(item, "Cannot create item: is null");
        final User user = userService.getUser(userId);
        if (item.getRequest() != null) {
            itemRequestService.getItemRequest(item.getRequest().getId());
//            ItemRequest itemRequest = itemRequestService.getItemRequest(item.getRequest().getId());
            /*item.setRequest(itemRequest);*/
        }
        item.setOwner(user);
        final Item createdItem = itemRepository.save(item);
        log.info("Created item with id = {}: {}", createdItem.getId(), createdItem);
        return createdItem;
    }

    @Override
    public Item getItem(long id, long userId) {
        return itemRepository.findByIdWithRelations(id)
                .map(item -> hiddenUserData(item, userId))
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public List<Item> getItems(long userId) {
        return itemRepository.findByOwnerId(userId, Sort.by("id")).stream()
                .map(item -> hiddenUserData(item, userId))
                .toList();
    }

    @Override
    public Item updateItem(long userId, Item updatedItemDto, long itemId) {
        Objects.requireNonNull(updatedItemDto, "Cannot update item: is null");
        userService.getUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessErrorException("Item can be updated only by its owner");
        }

        String name = updatedItemDto.getName();
        String description = updatedItemDto.getDescription();
        Boolean available = updatedItemDto.getAvailable();

        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (available != null) {
            item.setAvailable(available);
        }

        return itemRepository.save(item);
    }

    @Override
    public List<Item> getItemsByUser(Long userId) {
        return itemRepository.findAllByOwnerId(userId);
    }

    @Override
    public List<Item> searchItems(String text, long userId) {
        return "".equals(text) ? List.of() : itemRepository.findByNameOrDescription(text, Sort.by("id")).stream()
                .map(item -> hiddenUserData(item, userId))
                .toList();
    }

    @Override
    public void deleteItem(long id, long userId) {
        itemRepository.findByIdWithRelations(id)
                .filter(item -> !Objects.equals(item.getOwner().getId(), userId))
                .ifPresent(item -> {
                    throw new AccessErrorException("Only owner can delete item");
                });
        if (itemRepository.delete(id) != 0) {
            log.info("Deleted item with id = {}", id);
        } else {
            log.info("No item deleted: item with id = {} does not exist", id);
        }
    }


    @Override
    public Item getItemToBook(final long id, final long userId) {
        return itemRepository.findById(id)
                .filter(item -> !Objects.equals(item.getOwner().getId(), userId))
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public boolean existByOwnerId(long userId) {
        return itemRepository.existsByOwnerId(userId);
    }

    public boolean isItemAvailable(Long itemId, LocalDateTime start, LocalDateTime end) {
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(itemId, start, end);
        return overlappingBookings.isEmpty();
    }

    private Item hiddenUserData(final Item item, final long userId) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            item.setLastBooking(null);
            item.setNextBooking(null);
        }
        return item;
    }
}
