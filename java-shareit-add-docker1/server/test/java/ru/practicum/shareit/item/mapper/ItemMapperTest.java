package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.mapper.dto.ItemDtoIn;
import ru.practicum.shareit.item.mapper.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class ItemMapperTest {

    private final User owner = new User(1L, "name", "email@mail.ru");
    private final User requestor = new User(1L, "requestor", "requestor@mail.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", requestor, LocalDateTime.now());
    @Autowired
    private ItemMapper itemMapper;
    @Test
    void toItemDtoOutTest() {
        Item item = new Item(1L, "name", "description", true, owner);
        item.setRequest(itemRequest);

        ItemDtoOut itemDtoOut = itemMapper.toItemDtoOut(item);

        assertNotNull(itemDtoOut);
        assertEquals(item.getId(), itemDtoOut.getId());
        assertEquals(item.getName(), itemDtoOut.getName());
        assertEquals(item.getDescription(), itemDtoOut.getDescription());
        assertEquals(item.getAvailable(), itemDtoOut.getAvailable());
    }

    @Test
    void toItemTest() {
        ItemDtoIn itemDtoIn =
                new ItemDtoIn(1L, "name", "description", false, null);

        Item item = itemMapper.toItem(itemDtoIn);

        assertNotNull(item);
        assertEquals(itemDtoIn.getId(), item.getId());
        assertEquals(itemDtoIn.getName(), item.getName());
        assertEquals(itemDtoIn.getDescription(), item.getDescription());
        assertEquals(itemDtoIn.getAvailable(), item.getAvailable());
    }

}
