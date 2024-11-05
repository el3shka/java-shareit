package ru.practicum.shareit.utils;

import ru.practicum.shareit.booking.dto.NewBookingDtoRequest;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

public class DataUtils {


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

    public static NewBookingDtoRequest getNewBookingDtoRequestTestTransient(int nameVariable) {
        return NewBookingDtoRequest.builder()
                .bookerId(nameVariable)
                .itemId((long) nameVariable)
                .start(LocalDateTime.now().plusDays(nameVariable))
                .end(LocalDateTime.now().plusWeeks(nameVariable)).build();
    }

    public static ItemRequestDto getItemRequestDtoNullDescriptionTestTransient(int nameVariable) {
        return ItemRequestDto.builder()
                .userId((long) nameVariable)
                .build();
    }
}
