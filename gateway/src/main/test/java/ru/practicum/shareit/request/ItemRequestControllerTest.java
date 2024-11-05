package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.utils.DataUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.matcher.ResponseBodyMatcher.responseBody;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestClient client;
    private static final String URL = "/requests";
    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    @DisplayName("Test validation item request functionality. Description not be null.")
    void givenNewBookingDtoRequest_whenItemIdNull_throwException() throws Exception {
        //given
        ItemRequestDto itemRequestDto = DataUtils.getItemRequestDtoNullDescriptionTestTransient(1);
        String json = objectMapper.writeValueAsString(itemRequestDto);
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, "1")
                .content(json));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody()
                        .containsError("Invalid value of the createItemRequest.itemRequestDto.description" +
                                " parameter: Description must be not blank"));
    }
}