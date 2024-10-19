package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class ItemDetailsDto {

    private Long id;
    @NotBlank
    private String name;
    @NotNull
    @Size(max = 200)
    private String description;
    private Boolean available;
    private ItemRequest request;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentDto> comments = new ArrayList<>();
}
