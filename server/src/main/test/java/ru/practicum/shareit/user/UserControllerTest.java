package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.DataUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.matcher.ResponseBodyMatcher.responseBody;

@WebMvcTest(controllers = UserController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static final String URL = "/users";
    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Test user get by id functionality")
    void givenUserDto_whenGetUserById_thenUserDtoReturned() throws Exception {
        //given
        UserDto userDto = DataUtils.getUserDtoTestPersistence(1);
        given(userService.getById(anyLong())).willReturn(userDto);
        //when
        ResultActions result = mvc.perform(get(URL + "/1")
                .contentType("application/json"));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsObjectAsJson(userDto, UserDto.class));
    }

    @Test
    @DisplayName("Test user get by id not found functionality")
    void givenUserDto_whenGetUserByIdNotFound_then404() throws Exception {
        //given
        given(userService.getById(anyLong())).willThrow(new NotFoundException("User with id 1 not found"));
        //when
        ResultActions result = mvc.perform(get(URL + "/1")
                .contentType("application/json"));
        //then
        result.andExpect(status().isNotFound())
                .andExpect(responseBody().containsError("User with id 1 not found"));
    }

    @Test
    @DisplayName("Test get all user functionality")
    void givenUserDto_whenGetAllUsers_thenUsersReturned() throws Exception {
        //given
        UserDto userDto1 = DataUtils.getUserDtoTestPersistence(1);
        UserDto userDto2 = DataUtils.getUserDtoTestPersistence(2);
        UserDto userDto3 = DataUtils.getUserDtoTestPersistence(3);
        List<UserDto> usersDto = List.of(userDto1, userDto2, userDto3);
        given(userService.getAll()).willReturn(usersDto);
        //when
        ResultActions result = mvc.perform(get(URL)
                .contentType("application/json"));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsListAsJson(usersDto, new TypeReference<List<UserDto>>() {
                }));
    }

    @Test
    @DisplayName("Test create user functionality")
    void givenUserDto_whenCreateUser_thenUserDtoReturned() throws Exception {
        //given
        UserDto user = DataUtils.getUserDtoTestTransient(1);
        String json = objectMapper.writeValueAsString(user);
        UserDto userDtoExpected = DataUtils.getUserDtoTestPersistence(1);
        given(userService.create(any(UserDto.class))).willReturn(userDtoExpected);
        //when
        ResultActions result = mvc.perform(post(URL)
                .contentType("application/json")
                .content(json));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsObjectAsJson(userDtoExpected, UserDto.class));
    }

    @Test
    @DisplayName("Test create user with duplicate email functionality")
    void givenUserDto_whenCreateUserWithDuplicateEmail_thenThrowException() throws Exception {
        //given
        UserDto user = DataUtils.getUserDtoTestTransient(1);
        String json = objectMapper.writeValueAsString(user);
        given(userService.create(any(UserDto.class)))
                .willThrow(new DuplicatedDataException("Email address already exists"));
        //when
        ResultActions result = mvc.perform(post(URL)
                .contentType("application/json")
                .content(json));
        //then
        result.andExpect(status().isConflict())
                .andExpect(responseBody().containsError(
                        "Email address already exists"));
    }

    @Test
    @DisplayName("Test delete user functionality")
    void deleteUser_thenReturn200() throws Exception {
        //given
        //when
        ResultActions result = mvc.perform(delete(URL + "/1"));
        //then
        result.andExpect(status().isOk());

    }

    @Test
    @DisplayName("Test user update functionality")
    public void givenUserDto_whenUpdateUser_thenUserDtoReturned() throws Exception {
        //given
        UserDto userDto = DataUtils.getUserDtoTestPersistence(1);
        String updateName = "update_name";
        userDto.setName(updateName);
        String json = objectMapper.writeValueAsString(userDto);
        given(userService.update(anyLong(), any(UserDto.class))).willReturn(userDto);
        //when
        ResultActions result = mvc.perform(patch(URL + "/1")
                .contentType("application/json")
                .content(json));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsObjectAsJson(userDto, UserDto.class));
    }

    @Test
    @DisplayName("Test update not found user functionality")
    public void givenUserDto_whenUpdateUserNotFound_then404() throws Exception {
        //given
        UserDto userDto = DataUtils.getUserDtoTestPersistence(1);
        String updateName = "update_name";
        userDto.setName(updateName);
        String json = objectMapper.writeValueAsString(userDto);
        given(userService.update(anyLong(), any(UserDto.class)))
                .willThrow(new NotFoundException("User with id 1 not found"));
        //when
        ResultActions result = mvc.perform(patch(URL + "/1")
                .contentType("application/json")
                .content(json));
        //then
        result.andExpect(status().isNotFound())
                .andExpect(responseBody().containsError("User with id 1 not found"));
    }

    @Test
    @DisplayName("Test update user duplicate email functionality")
    public void givenUserDto_whenUpdateUserDuplicateEmail_then409() throws Exception {
        //given
        UserDto userDto = DataUtils.getUserDtoTestPersistence(1);
        String updateName = "update_name";
        userDto.setName(updateName);
        String json = objectMapper.writeValueAsString(userDto);
        given(userService.update(anyLong(), any(UserDto.class)))
                .willThrow(new DuplicatedDataException("Email address already exists"));
        //when
        ResultActions result = mvc.perform(patch(URL + "/1")
                .contentType("application/json")
                .content(json));
        //then
        result.andExpect(status().isConflict())
                .andExpect(responseBody().containsError("Email address already exists"));
    }
}