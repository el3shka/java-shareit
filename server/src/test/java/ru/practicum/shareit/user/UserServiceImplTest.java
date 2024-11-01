package ru.practicum.shareit.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Arrays;
import java.util.Optional;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
    }

    @Test
    void createUser_shouldSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertThat(createdUser).isEqualTo(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUser_whenUserExists_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUser(1L);

        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    void getUser_whenUserDoesNotExist_shouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUser(2L));
        assertThat(exception.getMessage()).contains("User with id 2 not found");
    }

    @Test
    void deleteUser_whenUserExists_shouldDeleteUser() {
        when(userRepository.delete(1L)).thenReturn(1);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(1L);
    }

    @Test
    void deleteUser_whenUserDoesNotExist_shouldLogInfo() {
        when(userRepository.delete(2L)).thenReturn(0);

        userService.deleteUser(2L);

        verify(userRepository, times(1)).delete(2L);
    }

    @Test
    void updateUser_whenUserExists_shouldUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(1L, user);

        assertThat(updatedUser).isEqualTo(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_whenUserDoesNotExist_shouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUser(2L, user));
        assertThat(exception.getMessage()).contains("User with id 1 not found");
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        assertThat(userService.getAllUsers()).containsExactly(user);
    }

    @Test
    void findUserById_whenUserExists_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.findUserById(1L);

        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    void findUserById_whenUserDoesNotExist_shouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.findUserById(3L));
        assertThat(exception.getMessage()).contains("User with id 3 not found");
    }

    @Test
    void existsById_whenUserExists_shouldReturnTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertThat(userService.existsById(1L)).isTrue();
    }

    @Test
    void existsById_whenUserDoesNotExist_shouldReturnFalse() {
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThat(userService.existsById(2L)).isFalse();
    }
}
