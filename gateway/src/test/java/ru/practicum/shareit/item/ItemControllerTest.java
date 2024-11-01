package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdatedDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ItemControllerTest {

    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private ItemController itemController;

    private ItemCreateDto itemCreateDto;
    private ItemUpdatedDto itemUpdatedDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация тестовых данных
        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");

        itemUpdatedDto = new ItemUpdatedDto();
        itemUpdatedDto.setName("Updated Test Item");
        itemUpdatedDto.setDescription("Updated Test Description");

        commentDto = new CommentDto();
        commentDto.setText("Nice item!");
    }

    @Test
    void createItem_validRequest_shouldReturnResponse() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemClient.createItem(eq(userId), any(ItemCreateDto.class))).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.createItem(userId, itemCreateDto);

        assertEquals(expectedResponse, response);
        verify(itemClient, times(1)).createItem(userId, itemCreateDto);
    }

    @Test
    void getItemById_validRequest_shouldReturnResponse() {
        long userId = 1L;
        long itemId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemClient.getItemById(itemId, userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.getItemById(userId, itemId);

        assertEquals(expectedResponse, response);
        verify(itemClient, times(1)).getItemById(itemId, userId);
    }

    @Test
    void getItemsByUser_validRequest_shouldReturnResponse() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemClient.getItemsByUser(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.getItemsByUser(userId);

        assertEquals(expectedResponse, response);
        verify(itemClient, times(1)).getItemsByUser(userId);
    }

    @Test
    void searchItems_validRequest_shouldReturnResponse() {
        long userId = 1L;
        String text = "Test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemClient.searchItems(userId, text)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.searchItems(text, userId);

        assertEquals(expectedResponse, response);
        verify(itemClient, times(1)).searchItems(userId, text);
    }

    @Test
    void addComment_validRequest_shouldReturnResponse() {
        long userId = 1L;
        long itemId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemClient.addComment(itemId, userId, commentDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.addComment(itemId, userId, commentDto);

        assertEquals(expectedResponse, response);
        verify(itemClient, times(1)).addComment(itemId, userId, commentDto);
    }

    @Test
    void updateItem_validRequest_shouldReturnResponse() {
        long userId = 1L;
        long itemId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemClient.updateItem(itemId, userId, itemUpdatedDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemController.updateItem(userId, itemUpdatedDto, itemId);

        assertEquals(expectedResponse, response);
        verify(itemClient, times(1)).updateItem(itemId, userId, itemUpdatedDto);
    }
}

