package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreateDto newUserDto) {
        log.info("Received POST at /users");
        return userClient.createUser(newUserDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@Positive @PathVariable Long userId) {
        log.info("Received GET at /users/{}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Received GET at /users");
        return userClient.getAllUsers();
    }

    @PatchMapping(value = "/{userId}")
    public ResponseEntity<Object> updateUser(@Positive @PathVariable Long userId,
                                             @RequestBody UserUpdateDto updatedUserDto) {
        log.info("Received PATCH at /users/{}: {}", userId, updatedUserDto);
        return userClient.updateUser(userId, updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@Positive @PathVariable Long userId) {
        log.info("Received DELETE at /users/{}", userId);
        return userClient.deleteUser(userId);
    }
}
