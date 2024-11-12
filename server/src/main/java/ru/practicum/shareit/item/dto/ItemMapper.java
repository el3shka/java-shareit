package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public ItemDto toDto(Item item) {
        if (item == null) return null;
        ItemDto.ItemDtoBuilder dtoBuilder = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable());
        if (item.getRequest() != null) {
            dtoBuilder.requestId(item.getRequest().getId());
        }
        return dtoBuilder.build();
    }

    public Item fromDto(ItemDto dto, User user, ItemRequest request) {
        if (dto == null) return null;
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setOwner(user);
        item.setAvailable(dto.getAvailable());
        if (request != null) {
            item.setRequest(request);
        }
        return item;
    }

    public List<ItemDto> toDtoList(List<Item> items) {
        if (items == null) return null;
        return items.stream().map(this::toDto).toList();
    }

    public ItemInfoDto toInfoDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        if (item == null) return null;
        ItemInfoDto.BookingDto last = null;
        ItemInfoDto.BookingDto next = null;

        List<ItemInfoDto.CommentDto> commentDtoList;
        if (comments != null) {
            commentDtoList = comments.stream()
                    .map(comment -> new ItemInfoDto.CommentDto(comment.getId(), comment.getText())).toList();
        } else {
            commentDtoList = new ArrayList<>();
        }
        if (lastBooking != null) {
            last = new ItemInfoDto.BookingDto(lastBooking.getId(),
                    lastBooking.getBooker().getId(),
                    lastBooking.getStartTime(),
                    lastBooking.getEndTime());
        }
        if (nextBooking != null) {
            next = new ItemInfoDto.BookingDto(nextBooking.getId(),
                    nextBooking.getBooker().getId(),
                    nextBooking.getStartTime(),
                    nextBooking.getEndTime());
        }
        return ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .lastBooking(last)
                .nextBooking(next)
                .comments(commentDtoList)
                .build();
    }

}
