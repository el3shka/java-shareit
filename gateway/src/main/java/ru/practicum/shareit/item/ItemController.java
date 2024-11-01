package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdatedDto;


@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody ItemCreateDto newItemDto) {
        log.info("Received POST at /items X-Sharer-User-Id={} {}", userId, newItemDto);
        return itemClient.createItem(userId, newItemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                              @Positive @PathVariable long itemId) {
        log.info("Received GET at /items/{}?X-Sharer-User-Id={}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET at /items?X-Sharer-User-Id={}", userId);
        return itemClient.getItemsByUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String text,
                                           @Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET at /items/search?text={}", text);
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive@PathVariable long itemId,
                                             @Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody CommentDto commentDto) {
        log.info("Received POST at /items/{}/comment X-Sharer-User-Id={} {}", itemId, userId, commentDto);
        return itemClient.addComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody ItemUpdatedDto itemDto,
                                             @Positive @PathVariable long itemId) {
        log.info("Received PATCH at /items/{}: {}", userId, itemDto);
        return itemClient.updateItem(itemId, userId, itemDto);
    }
}
