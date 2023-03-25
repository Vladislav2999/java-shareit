package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.dto.ItemDtoOut;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDtoOut;

public class ItemMapperTest {

    private final User owner = new User(1L, "name", "email@mail.ru");
    private final User requestor = new User(1L, "requestor", "requestor@mail.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", requestor, LocalDateTime.now());

    @Test
    void toItemDtoOutTest() {
        Item item = new Item(1L, "name", "description", true, owner);
        item.setRequest(itemRequest);

        ItemDtoOut itemDtoOut = toItemDtoOut(item);

        assertNotNull(itemDtoOut);
        assertEquals(item.getId(), itemDtoOut.getId());
        assertEquals(item.getName(), itemDtoOut.getName());
        assertEquals(item.getDescription(), itemDtoOut.getDescription());
        assertEquals(item.getAvailable(), itemDtoOut.getAvailable());
        assertEquals(item.getRequest().getId(), itemDtoOut.getRequestId());
    }

    @Test
    void toItemTest() {
        ItemDtoIn itemDtoIn =
                new ItemDtoIn(1L, "name", "description", false, null);

        Item item = toItem(itemDtoIn, owner);

        assertNotNull(item);
        assertNotNull(item.getOwner());
        assertEquals(owner.getId(), item.getOwner().getId());
        assertEquals(itemDtoIn.getId(), item.getId());
        assertEquals(itemDtoIn.getName(), item.getName());
        assertEquals(itemDtoIn.getDescription(), item.getDescription());
        assertEquals(itemDtoIn.getAvailable(), item.getAvailable());
    }

}
