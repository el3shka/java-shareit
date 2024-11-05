package ru.practicum.shareit.iiTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithItemInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ItemRequestIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRequestMapper itemRequestMapper;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("Test get item request by user functionality")
    void givenUserId_whenFindItemRequestByUserID_thenReturnItemRequest() {
        //given
        ItemRequestWithItemInfoDto expectedItemRequests = ItemRequestWithItemInfoDto.builder()
                .id(3L)
                .description("desc3")
                .build();

        //when
        List<ItemRequestWithItemInfoDto> itemRequestsByUser = itemRequestService.getItemRequestsByUser(3);
        //then
        assertThat(itemRequestsByUser).isNotEmpty()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(expectedItemRequests));
    }

    @Test
    @DisplayName("Test get item request by id functionality")
    void givenUserId_whenFindItemRequestById_thenReturnItemRequest() {
        //given
        ItemRequestWithItemInfoDto expectedDto = ItemRequestWithItemInfoDto.builder()
                .id(1L)
                .description("desc1")
                .build();
        //when
        ItemRequestWithItemInfoDto itemRequestById = itemRequestService.getItemRequestById(1);
        //then
        assertThat(itemRequestById).isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("Test get all item request functionality")
    void givenUserId_whenGetAllRequestPageable_thenReturnItemRequest() {
        //given
        ItemRequestDto expectedDto1 = ItemRequestDto.builder()
                .id(1)
                .description("desc1")
                .build();
        ItemRequestDto expectedDto2 = ItemRequestDto.builder()
                .id(2)
                .description("desc2")
                .build();
        ItemRequestDto expectedDto3 = ItemRequestDto.builder()
                .id(3)
                .description("desc3")
                .build();
        //when
        List<ItemRequestDto> allItemRequests = itemRequestService.getAllItemRequests(0, 3);
        //then
        assertThat(allItemRequests).isNotEmpty()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(expectedDto1, expectedDto2, expectedDto3));
    }

    @Test
    @DisplayName("Test create item request functionality")
    void givenUserId_whenCreateRequest_thenReturnItemRequest() {
        //given
        ItemRequestDto expectedDto = ItemRequestDto.builder()
                .id(4)
                .userId(4L)
                .description("desc4")
                .build();

        //when
        ItemRequestDto itemRequest = itemRequestService.createItemRequest(expectedDto);
        //then
        assertThat(itemRequest).isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedDto);
    }

}
