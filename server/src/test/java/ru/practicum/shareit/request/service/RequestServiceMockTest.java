package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exceptionHandler.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class RequestServiceMockTest {
    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;


    private ItemRequestDto itemRequestDto;

    private ItemRequest request;

    private User requestor;

    private Item item;

    @BeforeEach
    void beforeEach() {
        User owner = new User(1L, "owner", "owner@mail.ru");
        requestor = new User(2L, "requestor", "requestor@mail.ru");
        item = new Item(1L, "name", "description", true, owner);
        request = new ItemRequest(1L, "request", requestor, null);
        itemRequestDto = new ItemRequestDto(1L, "request", null, null);
    }

    @Test
    void createTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(request);

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> requestService.create(1L, itemRequestDto));

       Assertions.assertEquals("Пользователь с id 1 не найден",
                exception.getMessage());
    }

    @Test
    void createTestWrongUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> requestService.create(1L, itemRequestDto)
        );

        assertEquals("Пользователь с id 1 не найден",
                exception.getMessage());
    }

    @Test
    void findAllRequestsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestRepository.findAllByRequestorIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(request));

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> requestService.findAllRequests(2L, 0, 99));

        Assertions.assertEquals("Пользователь с id 2 не найден",
                exception.getMessage());
    }

    @Test
    void findAllRequestsTestUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> requestService.findAllRequests(2L, 0, 99)
        );

        assertEquals("Пользователь с id 2 не найден",
                exception.getMessage());
    }

    @Test
    void findAllByUserIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestRepository.findAllByRequestorIdOrderByCreatedAsc(anyLong()))
                .thenReturn(List.of(request));

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> requestService.findAllByUserId(2L));

        Assertions.assertEquals("Пользователь с id 2 не найден",
                exception.getMessage());
    }

    @Test
    void findAllByUserIdTestWrongUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> requestService.findAllByUserId(2L)
        );

        assertEquals("Пользователь с id 2 не найден",
                exception.getMessage());
    }

    @Test
    void findByRequestIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> requestService.findByRequestId(2L, 1L));


        Assertions.assertEquals("Пользователь с id 2 не найден",
                        exception.getMessage());
    }

    @Test
    void findByRequestIdTestUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> requestService.findByRequestId(2L, 1L)
        );

        assertEquals("Пользователь с id 2 не найден",
                exception.getMessage());
    }

    @Test
    void findByRequestIdTestRequestNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> requestService.findByRequestId(2L, 1L)
        );

        assertEquals("Запрос вещи с Id 1 не найден",
                exception.getMessage());
    }

}
