package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.ConflictException;
import ru.practicum.shareit.user.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final List<User> users;
    public UserServiceImpl() {
        this.users = new ArrayList<>();
    }

    @Override
    public User createUser(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Email cannot be null()");
        }
        users.stream().filter(user1 -> user1.getEmail().equals(user.getEmail()))
                .findFirst()
                .ifPresent(user1 -> {
                    throw new ConflictException("User with email " + user.getEmail() + " already exists");
                });
        Long newId = setCurrentId(); // Генерация нового ID
        user.setId(newId);
        log.info("Setting ID for new user: {}", newId);
        users.add(user);
        return user;
    }

    @Override
    public User getUser(Long userId) {
        return users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        users.removeIf(user -> user.getId().equals(userId));
    }

    @Override
    public User updateUser(User user) {
        Long userId = user.getId();
        User updatedUser = users.stream().filter(user1 -> user1.getId().equals(userId))
                .findFirst().orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        if (user.getEmail() != null) {
            users.stream().filter(user1 -> user1.getEmail().equals(user.getEmail()))
                    .findFirst()
                    .ifPresent(user1 -> {
                        throw new ConflictException("User with email " + user.getEmail() + " already exists");
                    });
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        return updatedUser;
    }

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    private Long setCurrentId() {
        return (long) (users.size() + 1);
    }
}
