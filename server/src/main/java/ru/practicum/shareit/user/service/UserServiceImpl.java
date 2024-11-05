package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        log.info("User with id {} found", id);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto dto) {
        try {
            User newUser = userRepository.save(userMapper.toNewUser(dto));
            log.info("User with id {} created. {}", newUser.getId(), newUser);
            return userMapper.toUserDto(newUser);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedDataException("Email address already exists");
        }
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserDtoList(users);
    }

    @Override
    public UserDto update(long id, UserDto dto) {
        log.info("Request to update user with id {} to {}", id, dto);
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        if (dto.getEmail() != null && !dto.getEmail().equals(userToUpdate.getEmail())) {
            userToUpdate.setEmail(dto.getEmail());
        }
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            userToUpdate.setName(dto.getName());
        }
        try {
            userRepository.save(userToUpdate);
            log.info("User with id {} updated {}", id, userToUpdate);
            return userMapper.toUserDto(userToUpdate);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedDataException("Email address already exists");
        }
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
        log.info("User with id {} deleted", id);
    }
}
