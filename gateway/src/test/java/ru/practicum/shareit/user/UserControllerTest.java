package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация тестовых DTO
        userCreateDto = new UserCreateDto();
        userCreateDto.setName("Test User");
        userCreateDto.setEmail("test@example.com");

        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Updated User");
        userUpdateDto.setEmail("updated@example.com");
    }

    @Test
    void createUser_validRequest_shouldReturnResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.createUser(any(UserCreateDto.class))).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.createUser(userCreateDto);

        assertEquals(expectedResponse, response);
        verify(userClient, times(1)).createUser(userCreateDto);
    }

    @Test
    void getUser_validId_shouldReturnResponse() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.getUser(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.getUser(userId);

        assertEquals(expectedResponse, response);
        verify(userClient, times(1)).getUser(userId);
    }

    @Test
    void getAllUsers_shouldReturnResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.getAllUsers()).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.getAllUsers();

        assertEquals(expectedResponse, response);
        verify(userClient, times(1)).getAllUsers();
    }

    @Test
    void updateUser_validRequest_shouldReturnResponse() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.updateUser(eq(userId), any(UserUpdateDto.class))).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.updateUser(userId, userUpdateDto);

        assertEquals(expectedResponse, response);
        verify(userClient, times(1)).updateUser(userId, userUpdateDto);
    }

    @Test
    void deleteUser_validId_shouldReturnResponse() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.deleteUser(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userController.deleteUser(userId);

        assertEquals(expectedResponse, response);
        verify(userClient, times(1)).deleteUser(userId);
    }
}

