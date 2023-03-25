package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private Item item;

    private ItemDtoIn itemDtoIn;

    private ItemDtoOut itemDtoOut;

    @BeforeEach
    void beforeEach() {
        User user = new User(1L, "name", "email@mail.ru");
        item = new Item(1L, "name", "description", true, user);
        itemDtoIn = new ItemDtoIn(null, "name", "description", true, null);
        itemDtoOut = new ItemDtoOut(1L, "name", "description", true, null);
    }

    @Test
    void createTest() throws Exception {
        when(itemService.create(any(ItemDtoIn.class), anyLong()))
                .thenReturn(itemDtoOut);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoOut)));
    }

    @Test
    void updateTest() throws Exception {
        ItemDtoOut itemDtoOutUpdated = new ItemDtoOut(
                1L,
                "nameUpdated",
                "descriptionUpdated",
                false,
                null
        );

        ItemDtoIn itemDtoInUpdated = new ItemDtoIn(
                null,
                "nameUpdated",
                "descriptionUpdated",
                false,
                null
        );

        when(itemService.update(Mockito.any(ItemDtoIn.class), anyLong()))
                .thenReturn(itemDtoOutUpdated);

        mockMvc.perform(patch("/items/" + item.getId())
                        .content(mapper.writeValueAsString(itemDtoInUpdated))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoOutUpdated)));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getByItemIdAndUserId(anyLong(), anyLong()))
                .thenReturn(itemDtoOut);

        mockMvc.perform(get("/items/" + item.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoOut)));

        verify(itemService, Mockito.times(1))
                .getByItemIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void getItemsTest() throws Exception {
        when(itemService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDtoOut));

        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));

        verify(itemService, Mockito.times(1))
                .getAll(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getItemsByTextTest() throws Exception {
        when(itemService.getByText(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDtoOut));

        mockMvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", "name")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));

        verify(itemService, Mockito.times(1))
                .getByText(anyString(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void createCommentTest() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "text", "name", LocalDateTime.now());
        CommentDto commentDtoIn = new CommentDto(null, "text", null, null);

        when(commentService.create(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/" + item.getId() + "/comment")
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));

        verify(commentService, Mockito.times(1))
                .create(any(CommentDto.class), anyLong(), anyLong());
    }

}
