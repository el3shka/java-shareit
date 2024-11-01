package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestRetrieveDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private ItemRequestMapper mapper;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private ItemRequestCreateDto createDto;
    private ItemRequest itemRequest;
    private ItemRequestRetrieveDto retrieveDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        createDto = new ItemRequestCreateDto();
        createDto.setDescription("Request Description");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Request Description");

        retrieveDto = new ItemRequestRetrieveDto();
        retrieveDto.setId(itemRequest.getId());
        retrieveDto.setDescription(itemRequest.getDescription());
    }

    @Test
    void createRequest_whenValidRequest_shouldReturnCreatedRequest() {
        when(mapper.mapToItemRequest(anyLong(), any(ItemRequestCreateDto.class))).thenReturn(itemRequest);
        when(itemRequestService.createItemRequest(any(ItemRequest.class))).thenReturn(itemRequest);
        when(mapper.mapToDto(any(ItemRequest.class))).thenReturn(retrieveDto);

        ItemRequestRetrieveDto result = itemRequestController.createRequest(1L, createDto);

        assertNotNull(result);
        assertEquals(retrieveDto.getId(), result.getId());
        verify(itemRequestService, times(1)).createItemRequest(itemRequest);
        verify(mapper, times(1)).mapToDto(itemRequest);
    }

    @Test
    void getRequestById_whenRequestExists_shouldReturnItemRequest() {
        when(itemRequestService.getItemRequestWithRelations(anyLong(), anyLong())).thenReturn(itemRequest);
        when(mapper.mapToDto(any(ItemRequest.class))).thenReturn(retrieveDto);

        ItemRequestRetrieveDto result = itemRequestController.getRequestById(itemRequest.getId(), 1L);

        assertNotNull(result);
        assertEquals(retrieveDto.getId(), result.getId());
        verify(itemRequestService, times(1)).getItemRequestWithRelations(itemRequest.getId(), 1L);
    }

    @Test
    void getRequestById_whenRequestNotExists_shouldThrowNotFoundException() {
        when(itemRequestService.getItemRequestWithRelations(anyLong(), anyLong())).thenThrow(new NotFoundException("Request not found"));

        assertThrows(NotFoundException.class, () -> itemRequestController.getRequestById(999L, 1L));
    }

    @Test
    void getOwnItemRequests_whenValidUserId_shouldReturnListOfRequests() {
        when(itemRequestService.getOwnRequests(anyLong())).thenReturn(Collections.singletonList(itemRequest));
        when(mapper.mapToDto(anyList())).thenReturn(Collections.singletonList(retrieveDto));

        List<ItemRequestRetrieveDto> result = itemRequestController.getOwnItemRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(retrieveDto.getId(), result.get(0).getId());
        verify(itemRequestService, times(1)).getOwnRequests(1L);
    }

    @Test
    void getOthersItemRequests_whenValidUserId_shouldReturnListOfRequests() {
        when(itemRequestService.getOthersRequests(anyLong())).thenReturn(Collections.singletonList(itemRequest));
        when(mapper.mapToDto(anyList())).thenReturn(Collections.singletonList(retrieveDto));

        List<ItemRequestRetrieveDto> result = itemRequestController.getOthersItemRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(retrieveDto.getId(), result.get(0).getId());
        verify(itemRequestService, times(1)).getOthersRequests(1L);
    }
}
