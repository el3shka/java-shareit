package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequestCreateDto {

    @NotBlank
    @Size(max = 2000)
    private String description;
}
