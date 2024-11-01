package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.exception.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        Objects.requireNonNull(user, "Cannot create user: is null");
        return userRepository.save(user);
    }

    @Override
    public User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " +
                userId + " not found"));
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        if (userRepository.delete(userId) != 0) {
            log.info("Deleted user with id = {}", userId);
        } else {
            log.info("No user deleted: user with id = {} does not exist", userId);
        }
    }

    @Override
    @Transactional
    public User updateUser(long userId, User updateUser) {
        Objects.requireNonNull(updateUser, "Cannot update user: is null");
        User exsistingUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " +
                "with id " + updateUser.getId() + " not found"));
        Optional.ofNullable(updateUser.getName()).ifPresent(exsistingUser::setName);
        Optional.ofNullable(updateUser.getEmail()).ifPresent(exsistingUser::setEmail);
        User updatedUser = userRepository.save(exsistingUser);
        log.info("Responded to PUT /users/{}{}", userId, updatedUser);
        return updatedUser;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " +
                userId + " not found"));
    }

    @Override
    public boolean existsById(final long id) {
        return userRepository.existsById(id);
    }

}
