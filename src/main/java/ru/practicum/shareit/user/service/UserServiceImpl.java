package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.exception.*;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ConflictException("User with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " +
                userId + " not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User updateUser(User user) {
        User exsistingUser = userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("User " +
                "with id " + user.getId() + " not found"));
        if (user.getEmail() != null) {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new ConflictException("User with email " + user.getEmail() + " already exists");
            }
            exsistingUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            exsistingUser.setName(user.getName());
        }
        return userRepository.save(exsistingUser);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
