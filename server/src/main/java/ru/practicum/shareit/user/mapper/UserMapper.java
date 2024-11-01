package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserRetrieveDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User mapToUser(UserCreateDto dto);

    User mapToUser(UserUpdateDto dto);

    UserRetrieveDto mapToDto(User user);

    List<UserRetrieveDto> mapToDto(List<User> users);
}
