package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("user@example.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequester(user);
        itemRequest.setDescription("Request Description");
        itemRequest.setCreated(LocalDateTime.now());
    }

    @Test
    void createItemRequest_whenValidRequest_shouldReturnCreatedRequest() {
        when(validator.validate(any(ItemRequest.class))).thenReturn(Collections.emptySet());
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequest createdRequest = itemRequestService.createItemRequest(itemRequest);

        assertNotNull(createdRequest);
        assertEquals(itemRequest.getId(), createdRequest.getId());
        verify(requestRepository, times(1)).save(itemRequest);
    }

    @Test
    void createItemRequest_whenRequestIsNull_shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> itemRequestService.createItemRequest(null));
    }

    @Test
    void createItemRequest_whenValidationFails_shouldThrowConstraintViolationException() {
        Set<ConstraintViolation<ItemRequest>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));

        when(validator.validate(any(ItemRequest.class))).thenReturn(violations);

        assertThrows(ConstraintViolationException.class, () -> itemRequestService.createItemRequest(itemRequest));
    }

    @Test
    void createItemRequest_whenRequesterIdIsNull_shouldThrowNullPointerException() {
        itemRequest.setRequester(null);
        assertThrows(NullPointerException.class, () -> itemRequestService.createItemRequest(itemRequest));
    }

    @Test
    void getItemRequest_whenRequestExists_shouldReturnItemRequest() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequest result = itemRequestService.getItemRequest(itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        verify(requestRepository, times(1)).findById(itemRequest.getId());
    }

    @Test
    void getItemRequest_whenRequestNotExists_shouldThrowNotFoundException() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(999L));
    }

    @Test
    void getRequestById_whenUserExists_shouldReturnItemRequest() {
        when(userService.findUserById(anyLong())).thenReturn(user);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequest result = itemRequestService.getRequestById(user.getId(), itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        verify(userService, times(1)).findUserById(user.getId());
        verify(requestRepository, times(1)).findById(itemRequest.getId());
    }

    @Test
    void getRequestById_whenUserNotExists_shouldThrowNotFoundException() {
        when(userService.findUserById(anyLong())).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(999L, itemRequest.getId()));
    }

    @Test
    void getRequestById_whenRequestNotExists_shouldThrowNotFoundException() {
        when(userService.findUserById(anyLong())).thenReturn(user);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(user.getId(), 999L));
    }

    @Test
    void getOwnRequests_whenUserExists_shouldReturnRequests() {
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestRepository.findAllByRequesterId(anyLong(), any()))
                .thenReturn(Collections.singletonList(itemRequest));

        List<ItemRequest> requests = itemRequestService.getOwnRequests(user.getId());

        assertNotNull(requests);
        assertEquals(1, requests.size());
        verify(requestRepository, times(1)).findAllByRequesterId(user.getId(),
                Sort.by(Sort.Direction.DESC, "created"));
    }

    @Test
    void getOthersRequests_whenUserExists_shouldReturnRequests() {
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestRepository.findAllOtherByRequesterId(anyLong(), any()))
                .thenReturn(Collections.singletonList(itemRequest));

        List<ItemRequest> requests = itemRequestService.getOthersRequests(user.getId());

        assertNotNull(requests);
        assertEquals(1, requests.size());
        verify(requestRepository, times(1)).findAllOtherByRequesterId(user.getId(),
                Sort.by(Sort.Direction.DESC, "created"));
    }
}

