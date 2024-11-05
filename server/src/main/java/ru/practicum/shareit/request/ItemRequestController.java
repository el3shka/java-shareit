package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                            @RequestHeader(USER_ID) long userId) {
        itemRequestDto.setUserId(userId);
        return itemRequestService.createItemRequest(itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestWithItemInfoDto> getItemRequestsByUser(@RequestHeader(USER_ID) long userId) {
        return itemRequestService.getItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestParam int from,
                                                   @RequestParam int size) {
        return itemRequestService.getAllItemRequests(from, size);
    }

    @GetMapping("/{id}")
    public ItemRequestWithItemInfoDto getItemRequestById(@PathVariable long id) {
        return itemRequestService.getItemRequestById(id);
    }
}
