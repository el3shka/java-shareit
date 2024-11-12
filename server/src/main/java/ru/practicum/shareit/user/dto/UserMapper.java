package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.List;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        if (user == null) return null;
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public User toNewUser(UserDto userDto) {
        if (userDto == null) return null;
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public List<UserDto> toUserDtoList(List<User> users) {
        if (users == null) return null;
        return users.stream().map(this::toUserDto).toList();
    }
}
