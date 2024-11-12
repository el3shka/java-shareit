package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validationMarker.Marker;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDtoRequest {
    @NotBlank(message = "Comment must be not null", groups = Marker.OnCreate.class)
    private String text;
    private long itemId;
    private long userId;
}
