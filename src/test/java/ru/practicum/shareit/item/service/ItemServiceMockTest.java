package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptionHandler.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.dto.ItemDtoOut;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


public class ItemServiceMockTest {

    private ItemRepository itemRepository;

    private ItemService itemService;

    private UserRepository userRepository;

    private RequestRepository requestRepository;

    private User user;

    private User secondUser;

    private Item item;

    private ItemDtoIn itemDto;

    @BeforeEach
    void beforeEach() {
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        requestRepository = Mockito.mock(RequestRepository.class);
        itemService = new ItemServiceImpl(
                requestRepository,
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository
        );

        user = new User(1L, "testName", "test@mail.com");

        secondUser = new User(2L, "testName2nd", "test2nd@mail.com");

        item = new Item(1L, "testName", "testDescription", true, user);

        itemDto = new ItemDtoIn(
                null, "testName", "testDescription", true, null);

    }

    @Test
    void createTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        ItemDtoOut savedItem = itemService.create(itemDto, user.getId());

        Assertions.assertNotNull(savedItem);
        Assertions.assertEquals(item.getId(), savedItem.getId());
        Assertions.assertEquals(itemDto.getName(), savedItem.getName());
        Assertions.assertEquals(itemDto.getName(), savedItem.getName());
        Assertions.assertEquals(itemDto.getAvailable(), savedItem.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), savedItem.getRequestId());
    }

    @Test
    void createTestWithWrongUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemService.create(itemDto, 1L));

        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }

    @Test
    void createTestWithItemRequest() {
        ItemRequest itemRequest =
                new ItemRequest(1L, "testDescription", secondUser, LocalDateTime.now());
        item.setRequest(itemRequest);
        itemDto.setRequestId(1L);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemDtoOut savedItem = itemService.create(itemDto, user.getId());

        Assertions.assertEquals(itemDto.getRequestId(), savedItem.getRequestId());
    }

    @Test
    void createTestWithWrongItemRequest() {
        itemDto.setRequestId(1L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemService.create(itemDto, 1L));

        Assertions.assertEquals("Запрос вещи, переданный в параметре не найден", exception.getMessage());
    }

    @Test
    void updateTest() {
        ItemDtoIn itemDto = new ItemDtoIn(1L, "updatedName", "updatedDescription", false, null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemDtoOut updatedItem = itemService.update(itemDto, user.getId());

        Assertions.assertNotNull(updatedItem);
        Assertions.assertEquals(item.getId(), updatedItem.getId());
        Assertions.assertEquals(itemDto.getName(), updatedItem.getName());
        Assertions.assertEquals(itemDto.getName(), updatedItem.getName());
        Assertions.assertEquals(itemDto.getAvailable(), updatedItem.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), updatedItem.getRequestId());
    }

    @Test
    void updateTestWithWrongUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemService.update(itemDto, 1L));

        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }

    @Test
    void updateTestWrongItemId() {
        ItemDtoIn itemDto = new ItemDtoIn(1L, "updatedName", "updatedDescription", false, null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemService.update(itemDto, 1L));

        Assertions.assertEquals("вещь с id 1 не найдена", exception.getMessage());
    }

    @Test
    void getByTextTestWithEmptyParam() {
        String text = "";

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<ItemDtoOut> foundItems = itemService.getByText(text, user.getId(), 0, 10);

        Assertions.assertEquals(0, foundItems.size());
    }

    @Test
    void getByTextTest() {
        String text = "tEsT";

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.search(Mockito.anyString(), Mockito.any()))
                .thenReturn(List.of(item));

        List<ItemDtoOut> foundItems = itemService.getByText(text, user.getId(), 0, 10);

        Mockito.verify(itemRepository, Mockito.times(1))
                .search(Mockito.anyString(), Mockito.any());

        Assertions.assertEquals(1, foundItems.size());
    }

}
