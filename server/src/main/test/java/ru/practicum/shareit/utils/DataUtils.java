package ru.practicum.shareit.utils;

import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.NewBookingDtoRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

public class DataUtils {
    public static User getUserTestTransient(int nameVariable) {
        User user = new User();
        user.setName("Test" + nameVariable);
        user.setEmail("Test" + nameVariable + "@test.com");
        return user;
    }

    public static User getUserTestPersistence(int nameVariable) {
        User user = new User();
        user.setId((long) nameVariable);
        user.setName("Test" + nameVariable);
        user.setEmail("Test" + nameVariable + "@test.com");
        return user;
    }

    public static UserDto getUserDtoTestTransient(int nameVariable) {
        UserDto userDto = new UserDto();
        userDto.setName("Test" + nameVariable);
        userDto.setEmail("Test" + nameVariable + "@test.com");
        return userDto;
    }

    public static UserDto getUserDtoTestPersistence(int nameVariable) {
        UserDto userDto = new UserDto();
        userDto.setId((long) nameVariable);
        userDto.setName("Test" + nameVariable);
        userDto.setEmail("Test" + nameVariable + "@test.com");
        return userDto;
    }


    public static Item getItemTestTransient(int nameVariable) {
        Item item = new Item();
        item.setName("item" + nameVariable);
        item.setDescription("item" + nameVariable + "desc");
        item.setAvailable(true);
        return item;
    }

    public static Item getItemTestPersistence(int nameVariable) {
        Item item = new Item();
        item.setId((long) nameVariable);
        item.setName("item" + nameVariable);
        item.setDescription("item" + nameVariable + "desc");
        item.setAvailable(true);
        return item;
    }

    public static ItemDto getItemDtoTestTransient(int nameVariable) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test" + nameVariable);
        itemDto.setDescription("Test" + nameVariable);
        itemDto.setAvailable(true);
        return itemDto;
    }

    public static ItemDto getItemDtoTestPersistence(int nameVariable) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId((long) nameVariable);
        itemDto.setName("Test" + nameVariable);
        itemDto.setDescription("Test" + nameVariable);
        itemDto.setAvailable(true);
        return itemDto;
    }

    public static ItemInfoDto getItemInfoDtoTestTransient(int nameVariable) {
        return ItemInfoDto.builder()
                .name("Test" + nameVariable)
                .description("Test" + nameVariable)
                .available(true)
                .build();
    }

    public static ItemInfoDto getItemInfoDtoTestPersistence(int nameVariable) {
        return ItemInfoDto.builder()
                .id((long) nameVariable)
                .name("Test" + nameVariable)
                .description("Test" + nameVariable)
                .available(true)
                .build();
    }

    public static CommentDtoRequest getCommentDtoRequestTestTransient(int nameVariable) {
        return CommentDtoRequest.builder()
                .itemId(1)
                .text("comment" + nameVariable)
                .userId(1)
                .build();
    }

    public static CommentDtoResponse getCommentDtoResponseTestPersistence(int nameVariable) {
        return CommentDtoResponse.builder()
                .id(nameVariable)
                .text("comment" + nameVariable)
                .created(null)
                .authorName("test" + nameVariable)
                .build();
    }

    public static ItemRequestDto getItemRequestDtoTestTransient(int nameVariable) {
        return ItemRequestDto.builder()
                .userId((long) nameVariable)
                .description("test" + nameVariable)
                .build();
    }

    public static ItemRequestDto getItemRequestDtoTestPersistence(int nameVariable) {
        return ItemRequestDto.builder()
                .id(nameVariable)
                .userId((long) nameVariable)
                .description("test" + nameVariable)
                .build();
    }

    public static NewBookingDtoRequest getNewBookingDtoRequestTestTransient(int nameVariable) {
        return NewBookingDtoRequest.builder()
                .bookerId(nameVariable)
                .itemId((long) nameVariable)
                .start(LocalDateTime.now().plusDays(nameVariable))
                .end(LocalDateTime.now().plusWeeks(nameVariable)).build();
    }

    public static BookingDtoResponse getBookingDtoResponseTestPersistence(int nameVariable) {
        return BookingDtoResponse.builder()
                .id(nameVariable)
                .booker(null)
                .item(null)
                .start(LocalDateTime.now().plusDays(nameVariable))
                .end(LocalDateTime.now().plusWeeks(nameVariable))
                .status("REJECTED")
                .build();
    }

}
