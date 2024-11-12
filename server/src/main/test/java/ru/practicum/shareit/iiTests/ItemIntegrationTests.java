package ru.practicum.shareit.iiTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ItemIntegrationTests {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("Test get all items of owner functionality")
    void givenUserID_whenGetAllItemsOfOwner_thenReturnedListItemInfoDto() {
        //given
        Booking lastBooking = bookingRepository.findById(1L).get();
        Booking nextBooking = bookingRepository.findById(3L).get();
        Comment comment1Item1 = commentRepository.findById(1L).get();
        Comment comment2Item1 = commentRepository.findById(2L).get();
        Comment comment1Item3 = commentRepository.findById(3L).get();

        Item item1 = DataUtils.getItemTestPersistence(1);
        Item item2 = DataUtils.getItemTestPersistence(2);
        Item item3 = DataUtils.getItemTestPersistence(3);
        item3.setAvailable(false);
        ItemInfoDto infoDto1 = itemMapper.toInfoDto(item1, lastBooking, nextBooking, List.of(comment1Item1, comment2Item1));
        ItemInfoDto infoDto2 = itemMapper.toInfoDto(item2, null, null, null);
        ItemInfoDto infoDto3 = itemMapper.toInfoDto(item3, null, null, List.of(comment1Item3));

        List<ItemInfoDto> expectedItemInfoDtos = List.of(infoDto1, infoDto2, infoDto3);
        //when
        List<ItemInfoDto> allOfOwner = itemService.getAllOfOwner(1L);
        //then
        assertThat(allOfOwner).isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(expectedItemInfoDtos);
    }

    @Test
    @DisplayName("Test get all items of owner not found functionality")
    void givenUserID_whenGetAllItemsOfOwner_thenThrowException() {
        //given
        //when
        //then
        assertThrows(NotFoundException.class, () -> itemService.getAllOfOwner(99L));
    }

    @Test
    @DisplayName("Test search functionality")
    void givenTextSearch_whenFindByNameOrDescription_thenReturnedItemDto() {
        //given
        String text = "1desc";
        Item item = DataUtils.getItemTestPersistence(1);
        ItemDto itemDto = itemMapper.toDto(item);
        List<ItemDto> expectedItemDtos = List.of(itemDto);
        //when
        List<ItemDto> byNameByDescription = itemService.findByNameByDescription(text);
        //then
        assertThat(byNameByDescription).isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(expectedItemDtos);
    }

    @Test
    @DisplayName("Test search text blank functionality")
    void givenTextSearchBlank_whenFindByNameOrDescription_thenReturnedEmptyList() {
        //given
        String text = "";
        //when
        List<ItemDto> byNameByDescription = itemService.findByNameByDescription(text);
        //then
        assertThat(byNameByDescription).isEmpty();
    }

    @Test
    @DisplayName("Test get items by id functionality")
    void givenUserID_whenGetItemsByID_thenReturnedListItemInfoDto() {
        //given
        Booking lastBooking = bookingRepository.findById(5L).get();
        Booking nextBooking = bookingRepository.findById(6L).get();
        Comment comment = commentRepository.findById(4L).get();
        Item item = DataUtils.getItemTestPersistence(4);
        ItemInfoDto expectedItemInfoDto = itemMapper.toInfoDto(item, lastBooking, nextBooking, List.of(comment));

        //when
        ItemInfoDto byId = itemService.getById(4L, 2);
        //then
        assertThat(byId).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedItemInfoDto);
    }

    @Test
    @DisplayName("Test get items by id functionality")
    void givenUserID_whenGetItemsByIdNotOwner_thenReturnedListItemInfoDto() {
        //given
        Item item = DataUtils.getItemTestPersistence(2);
        ItemInfoDto expectedItemInfoDto = itemMapper
                .toInfoDto(item, null, null, new ArrayList<>());

        //when
        ItemInfoDto byId = itemService.getById(2L, 2);
        //then
        assertThat(byId).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedItemInfoDto);
    }

    @Test
    @DisplayName("Test create comment functionality")
    @Transactional
    void givenCommentDto_whenCreateComment_thenReturnedCommentDto() {
        //given
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .itemId(1)
                .userId(3)
                .text("commentTest")
                .build();
        CommentDtoResponse expectedCommentDto = CommentDtoResponse.builder()
                .id(5)
                .authorName("User3")
                .text("commentTest")
                .created(null)
                .build();

        //when
        CommentDtoResponse commentDtoResponse = itemService.addComment(commentDtoRequest);
        //then
        assertThat(commentDtoResponse).isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedCommentDto);
    }

    @Test
    @DisplayName("Test create item comment if not rent user functionality")
    @Transactional
    void givenCommentDto_whenCreateCommentNotRentUser_thenThrowException() {
        //given
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .itemId(1)
                .userId(1)
                .text("commentTest")
                .build();

        //when

        //then
        assertThrows(IllegalArgumentException.class, () -> itemService.addComment(commentDtoRequest));
    }

    @Test
    @DisplayName("Test create item comment not found user functionality")
    @Transactional
    void givenCommentDto_whenCreateCommentNotFoundUser_thenThrowException() {
        //given
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .itemId(1)
                .userId(55)
                .text("commentTest")
                .build();

        //when

        //then
        assertThrows(NotFoundException.class, () -> itemService.addComment(commentDtoRequest));
    }

    @Test
    @DisplayName("Test create item comment not found item functionality")
    @Transactional
    void givenCommentDto_whenCreateCommentNotFoundItem_thenThrowException() {
        //given
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .itemId(44)
                .userId(3)
                .text("commentTest")
                .build();

        //when

        //then
        assertThrows(NotFoundException.class, () -> itemService.addComment(commentDtoRequest));
    }

    @Test
    @DisplayName("Test update not found functionality")
    void givenTextSearch_whenUpdateItemNotFound_thenThrowException() {
        //given
        String text = "1desc";
        Item item = DataUtils.getItemTestPersistence(55);
        ItemDto itemDto = itemMapper.toDto(item);

        //when

        //then
        assertThrows(NotFoundException.class, () -> itemService.update(itemDto, 1L));
    }
}
