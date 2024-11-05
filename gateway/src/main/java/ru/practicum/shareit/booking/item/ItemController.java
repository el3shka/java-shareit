package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.validationMarker.Marker;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient client;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<Object> create(@RequestBody @Valid ItemDto itemDto, @RequestHeader(USER_ID) long userId) {
        return client.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    @Validated(Marker.OnUpdate.class)
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody @Valid ItemDto itemDto,
                                         @RequestHeader(USER_ID) long userId) {
        itemDto.setId(id);
        return client.update(itemDto, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id,
                                          @RequestHeader(USER_ID) long userId) {
        return client.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOfOwner(@RequestHeader(USER_ID) long userId) {
        return client.getAllOfOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByNameByDescription(@RequestParam String text,
                                                          @RequestHeader(USER_ID) long userId) {
        return client.findByNameByDescription(text, userId);
    }

    @PostMapping("/{id}/comment")
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<Object> createComment(@PathVariable Long id,
                                                @RequestBody @Valid CommentDtoRequest commentDtoRequest,
                                                @RequestHeader(USER_ID) long userId) {
        commentDtoRequest.setItemId(id);
        commentDtoRequest.setUserId(userId);
        return client.addComment(commentDtoRequest);
    }
}
