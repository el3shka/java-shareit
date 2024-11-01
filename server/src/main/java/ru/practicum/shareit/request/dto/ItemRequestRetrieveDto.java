package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;


@Data

public class ItemRequestRetrieveDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private Set<ItemRequestItemRetrieveDto> items;
}

