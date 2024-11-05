package ru.practicum.shareit.item.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

@Validated
public interface ItemService {

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId);

    ItemInfoDto getById(Long id, long userId);

    List<ItemInfoDto> getAllOfOwner(Long userId);

    List<ItemDto> findByNameByDescription(String text);

    CommentDtoResponse addComment(CommentDtoRequest commentDtoRequest);
}
