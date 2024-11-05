package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader(USER_ID) long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable Long id, @RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID) long userId) {
        itemDto.setId(id);
        return itemService.update(itemDto, userId);
    }

    @GetMapping("/{id}")
    public ItemInfoDto getById(@PathVariable Long id,
                               @RequestHeader(USER_ID) long userId) {
        return itemService.getById(id, userId);
    }

    @GetMapping
    public List<ItemInfoDto> getAllOfOwner(@RequestHeader(USER_ID) long userId) {
        return itemService.getAllOfOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByNameByDescription(@RequestParam String text) {
        return itemService.findByNameByDescription(text);
    }

    @PostMapping("/{id}/comment")
    public CommentDtoResponse createComment(@PathVariable Long id,
                                            @RequestBody CommentDtoRequest commentDtoRequest,
                                            @RequestHeader(USER_ID) long userId) {
        commentDtoRequest.setItemId(id);
        commentDtoRequest.setUserId(userId);
        return itemService.addComment(commentDtoRequest);
    }
}
