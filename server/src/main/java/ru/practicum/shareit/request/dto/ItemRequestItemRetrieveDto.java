package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class ItemRequestItemRetrieveDto {

    private Long id;
    private Long ownerId;
    private String name;
    private String description;
}
