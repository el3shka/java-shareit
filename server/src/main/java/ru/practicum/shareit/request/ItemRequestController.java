package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestRetrieveDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper mapper;

    @PostMapping
    public ItemRequestRetrieveDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestBody ItemRequestCreateDto itemRequestDto) {
        log.info("Received POST at /requests");
        final ItemRequest itemRequest = mapper.mapToItemRequest(userId, itemRequestDto);
        final ItemRequestRetrieveDto dto = mapper.mapToDto(itemRequestService.createItemRequest(itemRequest));
        log.info("Responded to POST /requests: {}", itemRequestDto);
        return dto;
    }

    @GetMapping("/{requestId}")
    public ItemRequestRetrieveDto getRequestById(@PathVariable long requestId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET at /requests/{}", requestId);
        ItemRequest itemRequest = itemRequestService.getItemRequestWithRelations(requestId, userId);
        ItemRequestRetrieveDto dto = mapper.mapToDto(itemRequest);
        log.info("Responded to GET /requests/{}: {}", requestId, dto);
        return dto;
    }

    @GetMapping
    public List<ItemRequestRetrieveDto> getOwnItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET at /requests for userId={}", userId);
        final List<ItemRequestRetrieveDto> dtos = mapper.mapToDto(itemRequestService.getOwnRequests(userId));
        log.info("Responded to GET /requests: {}", dtos);
        return dtos;
    }

    @GetMapping("/all")
    public List<ItemRequestRetrieveDto> getOthersItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET at /requests/all for userId={}", userId);
        final List<ItemRequestRetrieveDto> dtos = mapper.mapToDto(itemRequestService.getOthersRequests(userId));
        log.info("Responded to GET /requests/all: {}", dtos);
        return dtos;
    }
}
