package ru.practicum.shareit.user.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Validated
public interface UserService {
    UserDto getById(long id);

    UserDto create(UserDto dto);

    List<UserDto> getAll();

    UserDto update(long id, UserDto dto);

    void delete(long id);

}
