package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUser(Long userId);

    void deleteUser(Long userId);

    User updateUser(User user);

    List<User> getAllUsers();
}
