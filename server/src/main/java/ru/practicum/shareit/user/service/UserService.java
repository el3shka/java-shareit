package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUser(long userId);

    void deleteUser(long userId);

    User updateUser(long id, User user);

    List<User> getAllUsers();

    User findUserById(Long userId);

    boolean existsById(long id);

}
