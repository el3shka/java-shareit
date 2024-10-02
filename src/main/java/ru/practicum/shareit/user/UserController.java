package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserMapper mapper;
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("Received GET at /users");
        return mapper.toCollectionUserDto(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable final Long userId) {
        log.info("Received GET at /users/{}", userId);
        return mapper.toUserDto(userService.getUser(userId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto createUser(@Valid @RequestBody final UserDto newUserDto) {
        log.info("Received POST at /users");
        final User user = mapper.toUser(newUserDto);
        final UserDto userDto = mapper.toUserDto(userService.createUser(user));
        log.info("Responded to POST /users: {}", userDto);
        return userDto;
    }

    @PatchMapping(value = "/{userId}")
    public UserDto updateUser(@PathVariable final Long userId, @Valid @RequestBody final UserDto updatedUserDto) {
        log.info("Received PATCH at /users/{}: {}", userId, updatedUserDto);
        final User user = mapper.toUser(updatedUserDto);
        user.setId(userId);
        final UserDto userDto = mapper.toUserDto(userService.updateUser(user));
        log.info("Responded to PATCH /users: {}", userDto);
        return userDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable final Long userId) {
        log.info("Received DELETE at /users/{}", userId);
        userService.deleteUser(userId);
        log.info("Responded to DELETE /users/{}", userId);
    }
}
