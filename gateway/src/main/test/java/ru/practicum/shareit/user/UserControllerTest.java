package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.utils.DataUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.matcher.ResponseBodyMatcher.responseBody;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserClient client;
    private static final String URL = "/users";

    @Test
    @DisplayName("Test create user with blank functionality")
    void givenUserDto_whenCreateUserWithBlankName_thenThrowException() throws Exception {
        //given
        UserDto userTestUser = DataUtils.getUserDtoTestTransient(1);
        userTestUser.setName("");
        String json = objectMapper.writeValueAsString(userTestUser);
        //when
        ResultActions result = mvc.perform(post(URL)
                .contentType("application/json")
                .content(json));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody().containsError(
                        "Invalid value of the createUser.dto.name parameter: name not be blank"));
    }

    @Test
    @DisplayName("Test create user with incorrect Email functionality")
    void givenUserDto_whenCreateUserWithIncorrectEmail_thenThrowException() throws Exception {
        //given
        UserDto userTestUser = DataUtils.getUserDtoTestTransient(1);
        userTestUser.setEmail("email.email.com");
        String query = objectMapper.writeValueAsString(userTestUser);
        //when
        ResultActions result = mvc.perform(post(URL)
                .contentType("application/json")
                .content(query));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody().containsError(
                        "Invalid value of the createUser.dto.email parameter: incorrect email"));
    }

    @Test
    @DisplayName("Test create user with id not null functionality")
    void givenUserDto_whenCreateUserWithId_thenThrowException() throws Exception {
        //given
        UserDto userTestUser = DataUtils.getUserDtoTestPersistence(1);
        String query = objectMapper.writeValueAsString(userTestUser);
        //when
        ResultActions result = mvc.perform(post(URL)
                .contentType("application/json")
                .content(query));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody().containsError(
                        "Invalid value of the createUser.dto.id parameter: must be null"));
    }
}