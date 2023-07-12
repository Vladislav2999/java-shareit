package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptionHandler.exception.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.mapper.dto.ItemDtoIn;
import ru.practicum.shareit.item.mapper.dto.ItemDtoOut;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ItemServiceMockTest {


    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @MockBean
    private UserRepository userRepository;

    private RequestRepository requestRepository;

    private BookingMapper bookingMapper;

    private User user;

    private User secondUser;

    private Item item;

    private ItemDtoIn itemDtoIn;

    private CommentMapper commentMapper;
    private  ItemMapper itemMapper;


    @BeforeEach
    void beforeEach() {
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        requestRepository = Mockito.mock(RequestRepository.class);
        commentMapper=Mockito.mock(CommentMapper.class);
        itemMapper = Mockito.mock(ItemMapper.class);


        user = new User(1L, "testName", "test@mail.com");

        secondUser = new User(2L, "testName2nd", "test2nd@mail.com");

        item = new Item(1L, "testName", "testDescription", true, user);

        itemDtoIn = new ItemDtoIn(
                null, "testName", "testDescription", true, null);


    }

    @Test
    void createTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemService.create(itemDtoIn, 1L));

        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }


    @Test
    void createTestWithWrongUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemService.create(itemDtoIn, 1L));

        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }

    @Test

    void createTestWithItemRequest() {
        ItemRequest itemRequest =
                new ItemRequest(1L, "testDescription", secondUser, LocalDateTime.now());
        item.setRequest(itemRequest);
        itemDtoIn.setRequestId(1L);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                ()->itemService.create(itemDtoIn, 1L));

        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }

    @Test
    void createTestWithWrongItemRequest() {
        itemDtoIn.setRequestId(1L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemService.create(itemDtoIn, 1L));

        //Assertions.assertEquals("Запрос вещи, переданный в параметре не найден", exception.getMessage());
    }

    @Test

    void updateTest() {
        ItemDtoIn itemDto = new ItemDtoIn(1L, "updatedName", "updatedDescription", false, null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,()->
                 itemService.update(itemDto, 1L));


    }

    @Test
    void updateTestWithWrongUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemService.update(itemDtoIn, 1L));

        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }

    @Test
    void updateTestWrongItemId() {
        ItemDtoIn itemDtoIn = new ItemDtoIn(1L, "updatedName", "updatedDescription", false, null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemService.update(itemDtoIn, 1L));
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

        List<ItemDtoOut> foundItems = itemService.getByText(text, user.getId(), 0, 10);

        Assertions.assertEquals(0, foundItems.size());
    }
}
