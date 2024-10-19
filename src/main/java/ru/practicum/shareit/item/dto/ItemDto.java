package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.model.ItemRequest;

@Getter
@Setter
@EqualsAndHashCode
public class ItemDto {

    private Long id;
    @NotBlank
    private String name;
    @NotNull
    @Size(max = 200)
    private String description;
    private Boolean available;
    private ItemRequest request;
}
