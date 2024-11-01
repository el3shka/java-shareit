package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentRetrieveDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemRetrieveDto;
import ru.practicum.shareit.item.dto.ItemUpdatedDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemMapper itemMapper;
    private final ItemService itemService;
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemRetrieveDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody ItemCreateDto newItemDto) {
        log.info("Received POST at /items");
        Item item = itemMapper.mapToItem(newItemDto);
        ItemRetrieveDto itemDto = itemMapper.mapToDto(itemService.createItem(item, userId));
        log.info("Responded to POST /items: {}", itemDto);
        return itemDto;
    }

    @GetMapping("/{itemId}")
    public ItemRetrieveDto getItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET at /items/{}", itemId);
        ItemRetrieveDto itemDto = itemMapper.mapToDto(itemService.getItem(itemId, userId));
        log.info("Responded to GET /items/{}: {}", itemId, itemDto);
        return itemDto;
    }

    @GetMapping
    public List<ItemRetrieveDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET at /items?X-Sharer-User-Id={}", userId);
        return itemMapper.mapToDto(itemService.getItems(userId));
    }

    @GetMapping("/search")
    public List<ItemRetrieveDto> searchItems(@RequestParam("text") String text,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET at /items/search?text={}", text);
        List<ItemRetrieveDto> items = itemMapper.mapToDto(itemService.searchItems(text, userId));
        log.info("Responded to GET /items/search?text={}: {}", text, items);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentRetrieveDto addComment(@PathVariable long itemId,
                                 @RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody @Validated CommentCreateDto commentDto) {
        log.info("Received POST at /items/{}/comment X-Sharer-User-Id={} {}", itemId, userId, commentDto);
        Comment comment = commentMapper.mapToComment(commentDto);
        CommentRetrieveDto commentDto1 = commentMapper.mapToDto(commentService.addComment(comment, itemId, userId));
        log.info("Responded to POST /items/{}/comment: {}", itemId, commentDto1);
        return commentDto1;
    }

    @PatchMapping("/{itemId}")
    public ItemRetrieveDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemUpdatedDto itemDto,
                              @PathVariable long itemId) {
        log.info("Received PATCH at /items/{} X-Sharer-User-Id={}", itemId, userId);
        Item item = itemMapper.mapToItem(itemDto);
        ItemRetrieveDto itemDto1 = itemMapper.mapToDto(itemService.updateItem(itemId, item, userId));
        log.info("Responded to PATCH /items/{}: {}", itemId, itemDto1);
        return itemDto1;
    }

    @DeleteMapping("/{id}")
    public void deleteItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long id) {
        log.info("Received DELETE at /items/{} X-Sharer-User-Id={}", id, userId);
        itemService.deleteItem(id, userId);
        log.info("Responded to DELETE /items/{} X-Sharer-User-Id={}", id, userId);
    }
}
