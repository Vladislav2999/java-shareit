package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.model.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.dto.ItemDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    private static final LocalDateTime commentTime = LocalDateTime.now().minusDays(1);

    private static final LocalDateTime START = LocalDateTime.now().minusDays(3);

    private static final LocalDateTime END = LocalDateTime.now().minusDays(2);

    private final ItemService itemService;

    private final UserService userService;

    private final RequestService itemRequestService;

    private final BookingService bookingService;

    private final CommentService commentService;

    private ItemDtoIn itemDtoIn;

    private ItemDtoIn secondItemDtoIn;

    private ItemRequestDto itemRequestDto;

    private User user;

    private User secondUser;

    private BookingDtoIn bookingDtoIn;

    private CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        itemDtoIn = new ItemDtoIn(
                null, "testName", "testDescription", true, null);

        secondItemDtoIn = new ItemDtoIn(
                null, "secondName", "secondDescription", true, null);

        itemRequestDto = new ItemRequestDto(null, "testDescription", null, null);

        user = new User(1L, "name", "email@mail.ru");

        secondUser = new User(2L, "secondName", "secondEmail@mail.ru");

        bookingDtoIn = new BookingDtoIn(1L, START, END, null);

        commentDto = new CommentDto(1L, "text", "booker", commentTime);
    }

    @Test
    void createTest() {
        User savedUser = userService.create(user);
        ItemRequestDto savedRequest = itemRequestService.create(savedUser.getId(), itemRequestDto);
        itemDtoIn.setRequestId(savedRequest.getId());
        User bookerUser = userService.create(secondUser);
        ItemDtoOut savedItem = itemService.create(itemDtoIn, savedUser.getId());

        bookingDtoIn.setItemId(savedItem.getId());
        bookingService.create(bookingDtoIn, bookerUser.getId());
        CommentDto savedComment = commentService.create(commentDto, savedItem.getId(), bookerUser.getId());
        ItemDtoOut foundItem = itemService.getByItemIdAndUserId(savedUser.getId(), savedItem.getId());

        assertNotNull(foundItem);
        assertEquals(
                savedItem.getId(),
                foundItem.getId());
        assertEquals(
                savedItem.getName(),
                foundItem.getName());
        assertEquals(
                savedItem.getDescription(),
                foundItem.getDescription());
        assertEquals(
                savedItem.getRequestId(),
                foundItem.getRequestId());
        assertEquals(
                savedComment.getText(),
                foundItem.getComments().get(0).getText());
    }

    @Test
    void updateTest() {
        User savedUser = userService.create(user);
        userService.create(secondUser);
        ItemDtoOut savedItem = itemService.create(itemDtoIn, savedUser.getId());
        ItemDtoIn itemForUpdate = new ItemDtoIn(
                savedItem.getId(), "updatedName", "updatedDescription", false, null);

        ItemDtoOut updatedItem = itemService.update(itemForUpdate, savedUser.getId());
        ItemDtoOut foundItem = itemService.getByItemIdAndUserId(savedUser.getId(), savedItem.getId());

        assertNotNull(foundItem);
        assertEquals(updatedItem.getId(), savedItem.getId());
        assertEquals(updatedItem.getName(), itemForUpdate.getName());
        assertEquals(updatedItem.getDescription(), itemForUpdate.getDescription());
        assertEquals(foundItem.getId(), updatedItem.getId());
    }

    @Test
    void getByIdTest() {
        User createdUser = userService.create(user);
        ItemDtoOut savedItem = itemService.create(itemDtoIn, createdUser.getId());
        ItemDtoOut foundItem = itemService.getByItemIdAndUserId(user.getId(), savedItem.getId());

        assertNotNull(foundItem);
        assertEquals(savedItem.getId(), foundItem.getId());
        assertEquals(savedItem.getName(), foundItem.getName());
        assertEquals(savedItem.getDescription(), foundItem.getDescription());
        assertEquals(savedItem.getRequestId(), foundItem.getRequestId());
    }

    @Test
    void getAllTest() {
        User savedUser = userService.create(user);
        userService.create(secondUser);
        ItemDtoOut firstItem = itemService.create(itemDtoIn, savedUser.getId());
        itemService.create(secondItemDtoIn, savedUser.getId());

        List<ItemDtoOut> foundItems = itemService.getAll(savedUser.getId(), 0, 999);

        assertNotNull(foundItems);
        assertEquals(2, foundItems.size());
        assertEquals(firstItem.getId(), foundItems.get(0).getId());
        assertEquals(firstItem.getName(), foundItems.get(0).getName());
        assertEquals(firstItem.getDescription(), foundItems.get(0).getDescription());
        assertEquals(firstItem.getAvailable(), foundItems.get(0).getAvailable());
    }

    @Test
    void getByTextTest() {
        ItemDtoIn itemShouldFindByDesc = new ItemDtoIn(
                null, "Name", "testDescription", true, null);
        ItemDtoIn itemNotAvailable = new ItemDtoIn(
                null, "testName", "testDescription", false, null);

        User savedUser = userService.create(user);
        ItemDtoOut firstItem = itemService.create(itemDtoIn, savedUser.getId());
        itemService.create(secondItemDtoIn, savedUser.getId());
        itemService.create(itemShouldFindByDesc, savedUser.getId());
        itemService.create(itemNotAvailable, savedUser.getId());

        List<ItemDtoOut> foundItems = itemService.getByText("tESt", savedUser.getId(), 0, 999);

        assertNotNull(foundItems);
        assertEquals(foundItems.size(), 2);
        assertEquals(foundItems.get(0).getId(), firstItem.getId());
        assertEquals(foundItems.get(0).getName(), firstItem.getName());
        assertEquals(foundItems.get(0).getDescription(), firstItem.getDescription());
        assertEquals(foundItems.get(0).getAvailable(), firstItem.getAvailable());
    }

}
