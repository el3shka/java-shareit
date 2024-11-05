package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.DataUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private ItemRequestRepository itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository,
                new ItemMapper(),
                new CommentMapper(), bookingRepository,
                commentRepository,
                itemRequestRepository);
    }

    @Test
    @DisplayName("Test create item functionality")
    public void givenItemDto_whenCreateItem_thenReturnItemDto() {
        //given
        Item item = DataUtils.getItemTestPersistence(1);
        User owner = DataUtils.getUserTestPersistence(1);
        item.setOwner(owner);
        given(itemRepository.save(any(Item.class))).willReturn(item);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(owner));
        ItemDto itemDtoCreate = DataUtils.getItemDtoTestTransient(1);
        //when
        ItemDto itemDtoCreated = itemService.create(itemDtoCreate, owner.getId());
        //then
        assertThat(itemDtoCreated).isNotNull();
        assertThat(itemDtoCreated.getId()).isEqualTo(item.getId());
    }

    @Test
    @DisplayName("Test create item with non-existent user functionality")
    public void givenItemDto_whenCreateItemWithNonExistentUser_thenThrowException() {
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        ItemDto itemDtoCreate = DataUtils.getItemDtoTestTransient(1);
        //when
        //then
        assertThrows(NotFoundException.class, () -> itemService.create(itemDtoCreate, 1L));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Test update item functionality")
    public void givenItemDto_whenUpdateItem_thenReturnItemDto() {
        //given
        String updateName = "updateName";
        Item item = DataUtils.getItemTestPersistence(1);
        User owner = DataUtils.getUserTestPersistence(1);
        item.setOwner(owner);
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(owner));
        given(userRepository.existsById(anyLong())).willReturn(true);
        item.setName(updateName);
        given(itemRepository.save(any(Item.class))).willReturn(item);
        ItemDto itemDtoUpdate = DataUtils.getItemDtoTestPersistence(1);
        itemDtoUpdate.setName(updateName);
        //when
        ItemDto itemDtoUpdated = itemService.update(itemDtoUpdate, owner.getId());
        //then
        assertThat(itemDtoUpdated).isNotNull();
        assertThat(itemDtoUpdated.getName()).isEqualTo(updateName);
    }

    @Test
    @DisplayName("Test update item with user non-existent functionality")
    public void givenItemDto_whenUpdateItemWithNonExistentUser_thenThrowException() {
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        ItemDto itemDtoUpdate = DataUtils.getItemDtoTestPersistence(1);
        //when
        //then
        assertThrows(NotFoundException.class, () -> itemService.update(itemDtoUpdate, 1L));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Test update item non existent functionality")
    public void givenItemDto_whenUpdateItemNonExistent_thenThrowException() {
        //given
        given(itemRepository.findById(anyLong())).willReturn(Optional.empty());
        ItemDto itemDtoUpdate = DataUtils.getItemDtoTestPersistence(1);
        //when
        //then
        assertThrows(NotFoundException.class, () -> itemService.update(itemDtoUpdate, 1L));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Test update item with user not owner functionality")
    public void givenItemDto_whenUpdateItemWithUserNotOwner_thenThrowException() {
        //given
        User owner = DataUtils.getUserTestPersistence(1);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(owner));
        given(userRepository.existsById(anyLong())).willReturn(true);
        Item item = DataUtils.getItemTestPersistence(1);
        item.setOwner(owner);
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item));
        ItemDto itemDtoUpdate = DataUtils.getItemDtoTestPersistence(1);
        //when
        //then
        assertThrows(AccessException.class, () -> itemService.update(itemDtoUpdate, 99L));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Test get item by id functionality")
    public void givenItemDto_whenGetItemById_thenItemDtoIsReturned() {
        //given
        User owner = DataUtils.getUserTestPersistence(1);
        Item item = DataUtils.getItemTestPersistence(1);
        item.setOwner(owner);
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item));
        //when
        ItemInfoDto byId = itemService.getById(1L, owner.getId());
        //then
        assertThat(byId).isNotNull();
    }

    @Test
    @DisplayName("Test get item by incorrect id functionality")
    public void givenItemDto_whenGetItemByIncorrectId_thenThrowException() {
        //given
        given(itemRepository.findById(anyLong())).willReturn(Optional.empty());
        //when
        //then
        assertThrows(NotFoundException.class, () -> itemService.getById(999L, 5L));
    }

    @Test
    @DisplayName("Test get item all by owner functionality")
    public void givenItemDto_whenGetAllByOwner_thenItemDtoIsReturned() {
        //given
        User owner1 = DataUtils.getUserTestPersistence(1);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(owner1));
        User owner2 = DataUtils.getUserTestPersistence(2);
        Item item1 = DataUtils.getItemTestPersistence(1);
        item1.setOwner(owner1);
        Item item2 = DataUtils.getItemTestPersistence(2);
        item2.setOwner(owner1);
        Item item3 = DataUtils.getItemTestPersistence(3);
        item3.setOwner(owner2);
        given(itemRepository.findAllByOwnerId(owner1.getId())).willReturn(List.of(item1, item2));
        given(userRepository.existsById(anyLong())).willReturn(true);
        //when
        List<ItemInfoDto> allOfOwner1Returned = itemService.getAllOfOwner(owner1.getId());
        List<ItemInfoDto> allOfOwner2Empty = itemService.getAllOfOwner(owner2.getId());
        //then
        assertThat(allOfOwner1Returned).isNotNull()
                .hasSize(2);
        assertThat(allOfOwner2Empty).isEmpty();
    }

    @Test
    @DisplayName("Test find item by name or by description functionality")
    public void givenItemDto_whenFindItemByNameByDescription_thenItemsDtoIsReturned() {
        //given
        Item item = DataUtils.getItemTestPersistence(1);
        given(itemRepository.findAllByNameOrDescription(anyString())).willReturn(List.of(item));
        //when
        List<ItemDto> itemsFound = itemService.findByNameByDescription("test1");
        //then
        assertThat(itemsFound).isNotNull()
                .hasSize(1);
    }

    @Test
    @DisplayName("Test find item by name or by description of empty query functionality")
    public void givenItemDto_whenFindItemByNameByDescriptionEmptyQuery_thenReturnEmpty() {
        //given
        //when
        List<ItemDto> itemsFound = itemService.findByNameByDescription("");
        //then
        assertThat(itemsFound).isEmpty();
        verify(itemRepository, never()).findAllByNameOrDescription(anyString());
    }

}