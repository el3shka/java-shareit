package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.AccessErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        owner = new User();
        owner.setId(1L);
        owner.setName("Test Owner");
        owner.setEmail("owner@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
    }

    @Test
    void createItem_whenValid_shouldReturnCreatedItem() {
        when(userService.getUser(anyLong())).thenReturn(owner);

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Item newItem = new Item();
        newItem.setName("New Item");
        newItem.setDescription("New Description");
        newItem.setAvailable(true);

        Item result = itemService.createItem(newItem, owner.getId());

        assertNotNull(result);

        assertEquals("New Item", result.getName());
        assertEquals("New Description", result.getDescription());

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository, times(1)).save(itemCaptor.capture());

        Item capturedItem = itemCaptor.getValue();
        assertEquals("New Item", capturedItem.getName());
        assertEquals("New Description", capturedItem.getDescription());
        assertTrue(capturedItem.getAvailable());
        assertEquals(owner, capturedItem.getOwner());
    }

    @Test
    void getItem_whenItemExists_shouldReturnItem() {
        when(itemRepository.findByIdWithRelations(anyLong())).thenReturn(Optional.of(item));

        Item result = itemService.getItem(item.getId(), owner.getId());

        assertNotNull(result);
        assertEquals("Test Item", result.getName());
        verify(itemRepository, times(1)).findByIdWithRelations(item.getId());
    }

    @Test
    void getItem_whenItemNotExists_shouldThrowNotFoundException() {
        when(itemRepository.findByIdWithRelations(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(999L, owner.getId()));
    }

    @Test
    void updateItem_whenOwnerIsValid_shouldUpdateItem() {
        Item updatedItem = new Item();
        updatedItem.setName("Updated Name");
        updatedItem.setDescription("Updated Description");
        updatedItem.setAvailable(false);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userService.getUser(anyLong())).thenReturn(owner);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.updateItem(owner.getId(), updatedItem, item.getId());

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.getAvailable());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItem_whenNotOwner_shouldThrowAccessErrorException() {
        User anotherUser = new User();
        anotherUser.setId(2L);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userService.getUser(anyLong())).thenReturn(anotherUser);

        Item updatedItem = new Item();
        assertThrows(AccessErrorException.class, () -> itemService.updateItem(anotherUser.getId(), updatedItem, item.getId()));
    }

    @Test
    void deleteItem_whenOwnerIsValid_shouldDeleteItem() {
        when(itemRepository.findByIdWithRelations(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.delete(anyLong())).thenReturn(1);

        itemService.deleteItem(item.getId(), owner.getId());

        verify(itemRepository, times(1)).delete(item.getId());
    }

    @Test
    void deleteItem_whenNotOwner_shouldThrowAccessErrorException() {
        User anotherUser = new User();
        anotherUser.setId(2L);

        when(itemRepository.findByIdWithRelations(anyLong())).thenReturn(Optional.of(item));

        assertThrows(AccessErrorException.class, () -> itemService.deleteItem(item.getId(), anotherUser.getId()));
    }

    @Test
    void searchItems_whenValidText_shouldReturnItems() {
        when(itemRepository.findByNameOrDescription(anyString(), any(Sort.class))).thenReturn(List.of(item));

        List<Item> result = itemService.searchItems("Test", owner.getId());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(itemRepository, times(1)).findByNameOrDescription("Test", Sort.by("id"));
    }

    @Test
    void searchItems_whenEmptyText_shouldReturnEmptyList() {
        List<Item> result = itemService.searchItems("", owner.getId());

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).findByNameOrDescription(anyString(), any(Sort.class));
    }

    @Test
    void createItem_whenUserDoesNotExist_shouldThrowNotFoundException() {
        long nonExistentUserId = 999L;
        Item newItem = new Item();
        newItem.setName("New Item");
        newItem.setDescription("New Description");
        newItem.setAvailable(true);

        when(userService.getUser(nonExistentUserId)).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> itemService.createItem(newItem, nonExistentUserId));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_whenItemNotFound_shouldThrowNotFoundException() {
        Item updatedItem = new Item();
        updatedItem.setName("Updated Name");
        updatedItem.setDescription("Updated Description");
        updatedItem.setAvailable(false);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(owner.getId(), updatedItem, item.getId()));
    }

    @Test
    void searchItems_whenItemsNotFound_shouldReturnEmptyList() {
        when(itemRepository.findByNameOrDescription(anyString(), any(Sort.class))).thenReturn(List.of());

        List<Item> result = itemService.searchItems("NonExistentItem", owner.getId());

        assertTrue(result.isEmpty());
        verify(itemRepository, times(1)).findByNameOrDescription("NonExistentItem", Sort.by("id"));
    }
}

