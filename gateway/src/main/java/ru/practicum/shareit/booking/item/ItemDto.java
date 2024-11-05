package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validationMarker.Marker;

/**
 * TODO Sprint add-controllers.
 */
@Data
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    @Null(message = "The ID must be null", groups = Marker.OnCreate.class)
    private Long id;
    @NotBlank(message = "The name should not be blank", groups = Marker.OnCreate.class)
    private String name;
    @NotNull(message = "The description should not be null", groups = Marker.OnCreate.class)
    private String description;
    @NotNull(message = "The available should not be null", groups = Marker.OnCreate.class)
    private Boolean available;
    private Long requestId;
}
