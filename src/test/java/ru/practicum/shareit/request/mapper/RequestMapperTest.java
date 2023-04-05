package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@ActiveProfiles("test")
public class RequestMapperTest {

    private final User requestor = new User(1L, "requestor", "requestor@mail.ru");

    @Autowired
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
    }
}
