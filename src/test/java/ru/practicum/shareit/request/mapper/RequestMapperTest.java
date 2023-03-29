package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;



public class RequestMapperTest {

    private final User requestor = new User(1L, "requestor", "requestor@mail.ru");

    private RequestMapper requestMapper;

    @Test
    void toItemRequestTest() {
        ItemRequestDto itemRequestDto =
                new ItemRequestDto(1L, "description", LocalDateTime.now(), null);

        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto);

        assertNotNull(itemRequest);
        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
    }

    @Test
    void toItemRequestDtoTest() {
        ItemRequest itemRequest = new ItemRequest(1L, "description", requestor, LocalDateTime.now());

        ItemRequestDto itemRequestDto = requestMapper.toItemRequestDto(itemRequest);

        assertNotNull(itemRequestDto);
        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
        assertEquals(Collections.emptyList(), itemRequestDto.getItems());
    }
}
