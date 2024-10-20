package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemMapper itemMapper;
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping("/{itemId}")
    public ItemDetailsDto getItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET at /items/{}", itemId);
        ItemDetailsDto itemDetailsDto = itemService.getItemDetails(itemId, userId);
        log.info("Responded to GET /items/{}: {}", itemId, itemDetailsDto);
        return itemDetailsDto;
    }

    @GetMapping
    public Collection<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET at /items?X-Sharer-User-Id={}", userId);
        List<ItemDto> items = itemMapper.toItemDtoList(itemService.getItemsByUser(userService.getUser(userId)));
        log.info("Responded to GET /items?X-Sharer-User-Id={}: {}", userId, items);
        return items;
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam("text") String text,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET at /items/search?text={}", text);
        List<ItemDto> items = itemMapper.toItemDtoList(itemService.searchItems(text));
        log.info("Responded to GET /items/search?text={}: {}", text, items);
        return items;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Validated @RequestBody ItemDto newItemDto) {
        log.info("Received POST at /items");
        log.info("Available value: {}", newItemDto.getAvailable());
        final Item item = itemMapper.toItem(newItemDto);
        item.setOwner(userService.getUser(userId));
        log.info("Responded to POST /items: {}", newItemDto);
        final ItemDto itemDto = itemMapper.toItemDto(itemService.createItem(userId, item));
        final Item savedItem = itemService.createItem(userId, item);
        log.info("Saved item: {}", savedItem);
        log.info("Created item DTO: {}", itemDto);
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        log.info("Received PATCH at /items/{}: {}", userId, itemDto);
        return itemMapper.toItemDto(itemService.updateItem(userService.getUser(userId), itemMapper.toItem(itemDto),
                itemId));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Valid CommentDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto.getText());
    }
}
