package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private ItemMapper itemMapper;

    @MockBean
    private CommentMapper commentMapper;

    @Test
    void createItem_shouldReturnCreatedItem() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Item1");
        itemCreateDto.setDescription("Description1");
        itemCreateDto.setAvailable(true);
        ItemRetrieveDto itemRetrieveDto = new ItemRetrieveDto();
        itemRetrieveDto.setId(1L);
        itemRetrieveDto.setName("Item1");
        itemRetrieveDto.setDescription("Description1");
        itemRetrieveDto.setAvailable(true);

        when(itemMapper.mapToItem(any(ItemCreateDto.class))).thenReturn(new Item());
        when(itemService.createItem(any(Item.class), anyLong())).thenReturn(new Item());
        when(itemMapper.mapToDto(any(Item.class))).thenReturn(itemRetrieveDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Item1\", \"description\": \"Description1\", \"available\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item1"))
                .andExpect(jsonPath("$.description").value("Description1"));

        verify(itemService, times(1)).createItem(any(Item.class), anyLong());
    }

    @Test
    void getItem_shouldReturnItem() throws Exception {
        ItemRetrieveDto itemRetrieveDto = new ItemRetrieveDto();
        itemRetrieveDto.setId(1L);
        itemRetrieveDto.setName("Item1");
        itemRetrieveDto.setDescription("Description1");
        itemRetrieveDto.setAvailable(true);

        when(itemService.getItem(anyLong(), anyLong())).thenReturn(new Item());
        when(itemMapper.mapToDto(any(Item.class))).thenReturn(itemRetrieveDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item1"))
                .andExpect(jsonPath("$.description").value("Description1"));

        verify(itemService, times(1)).getItem(1L, 1L);
    }

    @Test
    void getItemsByUser_shouldReturnListOfItems() throws Exception {
        ItemRetrieveDto itemRetrieveDto = new ItemRetrieveDto();
        itemRetrieveDto.setId(1L);
        itemRetrieveDto.setName("Item1");
        itemRetrieveDto.setDescription("Description1");
        itemRetrieveDto.setAvailable(true);
        List<ItemRetrieveDto> items = List.of(itemRetrieveDto);

        when(itemService.getItems(anyLong())).thenReturn(List.of(new Item()));
        when(itemMapper.mapToDto(anyList())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item1"))
                .andExpect(jsonPath("$[0].description").value("Description1"));

        verify(itemService, times(1)).getItems(1L);
    }

    @Test
    void searchItems_shouldReturnListOfItems() throws Exception {
        ItemRetrieveDto itemRetrieveDto = new ItemRetrieveDto();
        itemRetrieveDto.setId(1L);
        itemRetrieveDto.setName("Item1");
        itemRetrieveDto.setDescription("Description1");
        itemRetrieveDto.setAvailable(true);
        List<ItemRetrieveDto> items = List.of(itemRetrieveDto);

        when(itemService.searchItems(anyString(), anyLong())).thenReturn(List.of(new Item()));
        when(itemMapper.mapToDto(anyList())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "Item1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item1"))
                .andExpect(jsonPath("$[0].description").value("Description1"));

        verify(itemService, times(1)).searchItems("Item1", 1L);
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Great item!");
        CommentRetrieveDto commentRetrieveDto = new CommentRetrieveDto();
        commentRetrieveDto.setId(1L);
        commentRetrieveDto.setText("Great item!");
        commentRetrieveDto.setAuthorName("User1");

        when(commentMapper.mapToComment(any(CommentCreateDto.class))).thenReturn(new Comment());
        when(commentService.addComment(any(Comment.class), anyLong(), anyLong())).thenReturn(new Comment());
        when(commentMapper.mapToDto(any(Comment.class))).thenReturn(commentRetrieveDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Great item!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("User1"));

        verify(commentService, times(1)).addComment(any(Comment.class), anyLong(), anyLong());
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        ItemUpdatedDto itemUpdatedDto = new ItemUpdatedDto();
        itemUpdatedDto.setName("Updated Item");
        itemUpdatedDto.setDescription("Updated Description");
        itemUpdatedDto.setAvailable(true);
        ItemRetrieveDto itemRetrieveDto = new ItemRetrieveDto();
        itemRetrieveDto.setName("Updated Item");
        itemRetrieveDto.setDescription("Updated Description");
        itemRetrieveDto.setAvailable(true);

        when(itemMapper.mapToItem(any(ItemUpdatedDto.class))).thenReturn(new Item());
        when(itemService.updateItem(anyLong(), any(Item.class), anyLong())).thenReturn(new Item());
        when(itemMapper.mapToDto(any(Item.class))).thenReturn(itemRetrieveDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Item\", \"description\": \"Updated Description\", \"available\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"));

        verify(itemService, times(1)).updateItem(anyLong(), any(Item.class), anyLong());
    }

    @Test
    void deleteItem_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItem(1L, 1L);
    }
}

