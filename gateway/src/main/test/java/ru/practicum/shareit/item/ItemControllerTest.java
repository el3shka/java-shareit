package ru.practicum.shareit.item;

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

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemClient client;
    private static final String URL = "/items";
    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    @DisplayName("Test create item with null available functionality")
    public void givenItemDto_whenCreateItemWithNullAvailable_thenThrowException() throws Exception {
        //given
        ItemDto itemDto = DataUtils.getItemDtoTestTransient(1);
        itemDto.setAvailable(null);
        String json = objectMapper.writeValueAsString(itemDto);
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody()
                        .containsError("Invalid value of the create.itemDto.available parameter: " +
                                "The available should not be null"));
    }

    @Test
    @DisplayName("Test create item with null name functionality")
    public void givenItemDto_whenCreateItemWithNullName_thenThrowException() throws Exception {
        //given
        ItemDto itemDto = DataUtils.getItemDtoTestTransient(1);
        itemDto.setName(null);
        String json = objectMapper.writeValueAsString(itemDto);
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody()
                        .containsError("Invalid value of the create.itemDto.name parameter: " +
                                "The name should not be blank"));
    }

    @Test
    @DisplayName("Test create item with null description functionality")
    public void givenItemDto_whenCreateItemWithNullDescription_thenThrowException() throws Exception {
        //given
        ItemDto itemDto = DataUtils.getItemDtoTestTransient(1);
        itemDto.setDescription(null);
        String json = objectMapper.writeValueAsString(itemDto);
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody()
                        .containsError("Invalid value of the create.itemDto.description parameter: " +
                                "The description should not be null"));
    }

    @Test
    @DisplayName("Test create item with id not null functionality")
    public void givenItemDto_whenCreateItemWithNotNullID_thenThrowException() throws Exception {
        //given
        ItemDto itemDto = DataUtils.getItemDtoTestPersistence(1);
        String json = objectMapper.writeValueAsString(itemDto);
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody()
                        .containsError("Invalid value of the create.itemDto.id parameter: " +
                                "The ID must be null"));
    }
}