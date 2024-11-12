package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validationMarker.Marker;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ItemRequestDto {
    long id;
    @NotBlank(message = "Description must be not blank", groups = Marker.OnCreate.class)
    private String description;
    private Long userId;
    private LocalDateTime created;
}
