package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserRetrieveDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserMapper mapper;
    private final UserService userService;

    @PostMapping
    public UserRetrieveDto createUser(@RequestBody @Valid UserCreateDto newUserDto) {
        log.info("Received POST at /users");
        User user = mapper.mapToUser(newUserDto);
        UserRetrieveDto dto = mapper.mapToDto(userService.createUser(user));
        log.info("Responded to POST at /users");
        return dto;
    }

    @GetMapping("/{userId}")
    public UserRetrieveDto getUser(@PathVariable final long userId) {
        log.info("Received GET at /users/{}", userId);
        UserRetrieveDto dto = mapper.mapToDto(userService.getUser(userId));
        log.info("Responded to GET at /users/{}", userId);
        return dto;
    }

    @GetMapping
    public List<UserRetrieveDto> getAllUsers() {
        log.info("Received GET at /users");
        List<UserRetrieveDto> dtos = mapper.mapToDto(userService.getAllUsers());
        log.info("Responded to GET at /users");
        return dtos;
    }

    @PatchMapping(value = "/{userId}")
    public UserRetrieveDto updateUser(@PathVariable final long userId,
                                      @Valid @RequestBody final UserUpdateDto updatedUserDto) {
        log.info("Received PATCH at /users/{}", userId);
        final User user = mapper.mapToUser(updatedUserDto);
        final UserRetrieveDto dto = mapper.mapToDto(userService.updateUser(userId, user));
        log.info("Responded to PATCH at /users/{}", userId);
        return dto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable final long userId) {
        log.info("Received DELETE at /users/{}", userId);
        userService.deleteUser(userId);
        log.info("Responded to DELETE /users/{}", userId);
    }
}
