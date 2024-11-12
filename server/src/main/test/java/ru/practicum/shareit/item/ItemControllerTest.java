package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.matcher.ResponseBodyMatcher.responseBody;

@WebMvcTest(controllers = ItemController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private static final String URL = "/items";
    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    @DisplayName("Test create item functionality")
    public void givenItemDto_whenCreateItem_thenCreatedItem() throws Exception {
        //given
        ItemDto itemDto = DataUtils.getItemDtoTestTransient(1);
        String json = objectMapper.writeValueAsString(itemDto);
        ItemDto expectedItemDto = DataUtils.getItemDtoTestPersistence(1);
        given(itemService.create(any(ItemDto.class), anyLong())).willReturn(expectedItemDto);

        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsObjectAsJson(expectedItemDto, ItemDto.class));
    }

    @Test
    @DisplayName("Test create item with non-existent user functionality")
    public void givenItemDto_whenCreateItemWithNonExistentUser_thenThrowException() throws Exception {
        //given
        ItemDto itemDto = DataUtils.getItemDtoTestTransient(1);
        String json = objectMapper.writeValueAsString(itemDto);
        given(itemService.create(any(ItemDto.class), anyLong()))
                .willThrow(new NotFoundException("User with id - 1 not found"));
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isNotFound())
                .andExpect(responseBody().containsError("User with id - 1 not found"));
    }

    @Test
    @DisplayName("Test get item by id functionality")
    public void givenItemDto_whenGetItemById_thenItemDtoReturned() throws Exception {
        //given
        ItemInfoDto itemDto = DataUtils.getItemInfoDtoTestPersistence(1);
        given(itemService.getById(anyLong(), anyLong())).willReturn(itemDto);
        //when
        ResultActions result = mockMvc.perform(get(URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsObjectAsJson(itemDto, ItemInfoDto.class));
    }

    @Test
    @DisplayName("Test get item by id not found functionality")
    public void givenItemDto_whenGetItemByIdNotFound_thenThrowException() throws Exception {
        //given
        given(itemService.getById(anyLong(), anyLong())).willThrow(new NotFoundException("Item not found"));
        //when
        ResultActions result = mockMvc.perform(get(URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isNotFound())
                .andExpect(responseBody().containsError("Item not found"));
    }

    @Test
    @DisplayName("Test update item functionality")
    public void givenItemDto_whenUpdateItem_thenItemDtoUpdated() throws Exception {
        //given
        ItemDto itemDto = DataUtils.getItemDtoTestPersistence(1);
        itemDto.setName("Updated Name");
        String json = objectMapper.writeValueAsString(itemDto);
        given(itemService.update(any(ItemDto.class), anyLong())).willReturn(itemDto);
        //when
        ResultActions result = mockMvc.perform(patch(URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsObjectAsJson(itemDto, ItemDto.class));
    }

    @Test
    @DisplayName("Test update item by not owner functionality")
    public void givenItemDto_whenUpdateItemByNotOwner_thenThrowException() throws Exception {
        //given
        ItemDto itemDto = DataUtils.getItemDtoTestPersistence(1);
        itemDto.setName("Updated Name");
        String json = objectMapper.writeValueAsString(itemDto);
        given(itemService.update(any(ItemDto.class), anyLong())).willThrow(new AccessException("The user with the id  - 1 is not the owner"));
        //when
        ResultActions result = mockMvc.perform(patch(URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isNotFound())
                .andExpect(responseBody().containsError("The user with the id  - 1 is not the owner"));
    }

    @Test
    @DisplayName("Test update item by user not found functionality")
    public void givenItemDto_whenUpdateItemByUserNotFound_thenThrowException() throws Exception {
        //given
        ItemDto itemDto = DataUtils.getItemDtoTestPersistence(1);
        itemDto.setName("Updated Name");
        String json = objectMapper.writeValueAsString(itemDto);
        given(itemService.update(any(ItemDto.class), anyLong()))
                .willThrow(new NotFoundException("User not found"));
        //when
        ResultActions result = mockMvc.perform(patch(URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isNotFound())
                .andExpect(responseBody().containsError("User not found"));
    }

    @Test
    @DisplayName("Test get all items of owner functionality")
    public void givenItemDto_whenGetAllItemsOfOwner_thenItemsReturned() throws Exception {
        //given
        ItemInfoDto itemDto1 = DataUtils.getItemInfoDtoTestPersistence(1);
        ItemInfoDto itemDto2 = DataUtils.getItemInfoDtoTestPersistence(2);
        ItemInfoDto itemDto3 = DataUtils.getItemInfoDtoTestPersistence(3);
        List<ItemInfoDto> items = List.of(itemDto1, itemDto2, itemDto3);
        given(itemService.getAllOfOwner(anyLong())).willReturn(items);
        //when
        ResultActions result = mockMvc.perform(get(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsListAsJson(items, new TypeReference<List<ItemInfoDto>>() {
                }));
    }

    @Test
    @DisplayName("Test search item functionality")
    public void givenItemDto_whenSearchItems_thenItemsReturned() throws Exception {
        //given
        ItemDto itemDto1 = DataUtils.getItemDtoTestPersistence(1);
        List<ItemDto> items = List.of(itemDto1);
        given(itemService.findByNameByDescription(anyString())).willReturn(items);
        //when
        ResultActions result = mockMvc.perform(get(URL + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("text", "test")
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsListAsJson(items, new TypeReference<List<ItemDto>>() {
                }));
    }

    @Test
    @DisplayName("Test search item empty query functionality")
    public void givenItemDto_whenSearchItemsEmptyQuery_thenItemsNotReturned() throws Exception {
        //given
        given(itemService.findByNameByDescription(anyString())).willReturn(new ArrayList<>());
        //when
        ResultActions result = mockMvc.perform(get(URL + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("text", "1")
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsListAsJson(List.of(), new TypeReference<List<ItemDto>>() {
                }));
    }

    @Test
    @DisplayName("Test create comment functionality")
    void givenCommentDto_whenCreateComment_thenCreateCommentReturned() throws Exception {
        //given
        CommentDtoRequest commentDtoRequest = DataUtils.getCommentDtoRequestTestTransient(1);
        String json = objectMapper.writeValueAsString(commentDtoRequest);
        CommentDtoResponse commentDtoResponse = DataUtils.getCommentDtoResponseTestPersistence(1);
        given(itemService.addComment(any(CommentDtoRequest.class))).willReturn(commentDtoResponse);
        //when
        ResultActions result = mockMvc.perform(post(URL + "/1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1)
                .content(json));
        //then
        result.andExpect(status().isOk())
                .andExpect(responseBody().containsObjectAsJson(commentDtoResponse, CommentDtoResponse.class));
    }

    @Test
    @DisplayName("Test create comment not rent user functionality")
    void givenCommentDto_whenCreateCommentNotUserRent_thenStatus500() throws Exception {
        //given
        CommentDtoRequest commentDtoRequest = DataUtils.getCommentDtoRequestTestTransient(1);
        String json = objectMapper.writeValueAsString(commentDtoRequest);
        given(itemService.addComment(any(CommentDtoRequest.class)))
                .willThrow(new IllegalArgumentException("The user is not rent this item"));
        //when
        ResultActions result = mockMvc.perform(post(URL + "/1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1)
                .content(json));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody().containsError("The user is not rent this item"));
    }
}