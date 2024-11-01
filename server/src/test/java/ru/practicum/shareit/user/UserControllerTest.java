package ru.practicum.shareit.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserRetrieveDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private User user;
    private UserCreateDto userCreateDto;
    private UserRetrieveDto userRetrieveDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        userCreateDto = new UserCreateDto();
        userCreateDto.setName("Test User");
        userCreateDto.setEmail("test@example.com");

        userRetrieveDto = new UserRetrieveDto();
        userRetrieveDto.setId(1L);
        userRetrieveDto.setName("Test User");
        userRetrieveDto.setEmail("test@example.com");

        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Updated User");
        userUpdateDto.setEmail("updated@example.com");
    }

    @Test
    void createUser_shouldReturnUserRetrieveDto() throws Exception {
        when(mapper.mapToUser(any(UserCreateDto.class))).thenReturn(user);
        when(userService.createUser(any(User.class))).thenReturn(user);
        when(mapper.mapToDto(any(User.class))).thenReturn(userRetrieveDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test User\", \"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void getUser_whenUserExists_shouldReturnUserRetrieveDto() throws Exception {
        when(userService.getUser(anyLong())).thenReturn(user);
        when(mapper.mapToDto(any(User.class))).thenReturn(userRetrieveDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(userService, times(1)).getUser(1L);
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user));
        when(mapper.mapToDto(anyList())).thenReturn(Collections.singletonList(userRetrieveDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test User"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void updateUser_shouldReturnUpdatedUserRetrieveDto() throws Exception {
        when(mapper.mapToUser(any(UserUpdateDto.class))).thenReturn(user);
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(user);
        when(mapper.mapToDto(any(User.class))).thenReturn(userRetrieveDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated User\", \"email\":\"updated@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(userService, times(1)).updateUser(anyLong(), any(User.class));
    }

    @Test
    void deleteUser_whenUserExists_shouldReturnOk() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1L);
    }
}

