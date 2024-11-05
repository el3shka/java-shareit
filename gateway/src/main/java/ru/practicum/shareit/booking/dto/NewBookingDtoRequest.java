package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.validation.StartBeforeEnd;
import ru.practicum.shareit.validationMarker.Marker;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@StartBeforeEnd
public class NewBookingDtoRequest {
    @NotNull(message = "Item id must not be null", groups = Marker.OnCreate.class)
    private Long itemId;
    private long bookerId;
    @NotNull(message = "Start time booking must not be null", groups = Marker.OnCreate.class)
    @FutureOrPresent(message = "Start time cannot be in the past", groups = Marker.OnCreate.class)
    private LocalDateTime start;
    @NotNull(message = "End time booking must not be null", groups = Marker.OnCreate.class)
    @Future(message = "End time cannot be in the past", groups = Marker.OnCreate.class)
    private LocalDateTime end;
}
