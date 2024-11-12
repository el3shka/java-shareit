package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utils.DataUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private ItemRequestService itemRequestService;
    private static final String URL = "/requests";
    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    @DisplayName("Test create request functionality")
    void givenItemRequestDto_whenCreateItemRequest_thenItemRequestDtoReturned() throws Exception {
        //given
        ItemRequestDto itemRequestDto = DataUtils.getItemRequestDtoTestTransient(1);
        String json = objectMapper.writeValueAsString(itemRequestDto);
        ItemRequestDto itemRequestDtoWithID = DataUtils.getItemRequestDtoTestPersistence(1);
        given(itemRequestService.createItemRequest(any(ItemRequestDto.class))).willReturn(itemRequestDtoWithID);
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1)
                .content(json));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsObjectAsJson(itemRequestDtoWithID, ItemRequestDto.class));
    }

    @Test
    @DisplayName("Test get requests by user functionality")
    void givenUserId_whenGetItemRequestsByUser_thenStatusOkReturned() throws Exception {
        //given
        given(itemRequestService.getItemRequestsByUser(anyLong())).willReturn(anyList());
        //when
        ResultActions result = mockMvc.perform(get(URL)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test create requests by wrong user functionality")
    void givenItemRequestDto_whenCreateItemRequest_thenThrowException() throws Exception {
        //given
        ItemRequestDto itemRequestDto = DataUtils.getItemRequestDtoTestTransient(1);
        String json = objectMapper.writeValueAsString(itemRequestDto);
        given(itemRequestService.createItemRequest(any(ItemRequestDto.class))).willThrow(new NotFoundException("User not found"));
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1)
                .content(json));
        //then
        result.andExpect(status().isNotFound())
                .andExpect(responseBody().containsError("User not found"));
    }

    @Test
    @DisplayName("Test get all requests functionality")
    void givenItemRequestDto_whenGetAllRequests_thenItemRequestsDtoReturned() throws Exception {
        //given
        ItemRequestDto itemRequestDto1 = DataUtils.getItemRequestDtoTestPersistence(1);
        ItemRequestDto itemRequestDto2 = DataUtils.getItemRequestDtoTestPersistence(1);
        ItemRequestDto itemRequestDto3 = DataUtils.getItemRequestDtoTestPersistence(1);
        List<ItemRequestDto> itemRequestDtos = List.of(itemRequestDto1, itemRequestDto2, itemRequestDto3);
        given(itemRequestService.getAllItemRequests(anyInt(), anyInt())).willReturn(itemRequestDtos);
        //when
        ResultActions result = mockMvc.perform(get(URL + "/all")
                .param("from", "0")
                .param("size", "3"));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsListAsJson(itemRequestDtos, new TypeReference<List<ItemRequestDto>>() {
                }));
    }

    @Test
    @DisplayName("Test get request by id functionality")
    void givenItemRequestDto_whenGetRequestById_thenStatusOKReturned() throws Exception {
        //given
        given(itemRequestService.getItemRequestById(anyLong())).willReturn(any(ItemRequestWithItemInfoDto.class));
        //when
        ResultActions result = mockMvc.perform(get(URL + "/1")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test get request by wrong id functionality")
    void givenItemRequestDto_whenGetRequestById_thenThrowException() throws Exception {
        //given
        given(itemRequestService.getItemRequestById(anyLong())).willThrow(new NotFoundException("Item Request not found"));
        //when
        ResultActions result = mockMvc.perform(get(URL + "/1")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isNotFound())
                .andExpect(responseBody().containsError("Item Request not found"));
    }
}