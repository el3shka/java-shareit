package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ItemRequestControllerTest {

    @Mock
    private ItemRequestClient itemRequestClient;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private NewItemRequestDto newItemRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription("Test item request description");
    }

    @Test
    void createRequest_validRequest_shouldReturnResponse() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemRequestClient.createRequest(eq(userId), any(NewItemRequestDto.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestController.createRequest(userId, newItemRequestDto);

        assertEquals(expectedResponse, response);
        verify(itemRequestClient, times(1)).createRequest(userId, newItemRequestDto);
    }

    @Test
    void getAllRequestsByUser_validUserId_shouldReturnResponse() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemRequestClient.getAllRequestsOfOwner(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestController.getAllRequestsByUser(userId);

        assertEquals(expectedResponse, response);
        verify(itemRequestClient, times(1)).getAllRequestsOfOwner(userId);
    }

    @Test
    void getAllRequestsByOtherUsers_validUserId_shouldReturnResponse() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemRequestClient.getAllRequestsOfOtherUsers(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestController.getAllRequestsByOtherUsers(userId);

        assertEquals(expectedResponse, response);
        verify(itemRequestClient, times(1)).getAllRequestsOfOtherUsers(userId);
    }

    @Test
    void getRequestById_validRequest_shouldReturnResponse() {
        long userId = 1L;
        long requestId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemRequestClient.getRequestById(requestId, userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestController.getRequestById(requestId, userId);

        assertEquals(expectedResponse, response);
        verify(itemRequestClient, times(1)).getRequestById(requestId, userId);
    }
}

