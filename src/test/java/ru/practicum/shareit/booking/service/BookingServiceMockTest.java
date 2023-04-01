package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptionHandler.exception.BookingException;
import ru.practicum.shareit.exceptionHandler.exception.EntityNotFoundException;
import ru.practicum.shareit.exceptionHandler.exception.WrongBookingStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceMockTest {
    private static final LocalDateTime START = LocalDateTime.of(2023, 2, 3, 9, 0);

    private static final LocalDateTime END = LocalDateTime.of(2033, 2, 4, 9, 0);

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    private Item item;

    private User booker;

    private User owner;

    private User user;

    private Booking booking;

    private BookingDtoIn bookingDtoIn;

    @BeforeEach
    public void beforeEach() {
        owner = new User(1L, "name", "email@mail.ru");
        booker = new User(2L, "secondName", "secondEmail@mail.ru");
        user = new User(3L, "thirdName", "thirdEmail@mail.ru");
        item = new Item(1L, "name", "description", true, owner);
        booking = new Booking(1L, START, END, item, booker, Status.WAITING);
        bookingDtoIn = new BookingDtoIn(null, START, END, 1L);

    }

    @Test
    void createTest() {
        when(itemService.getItemById(eq(1L))).thenReturn(item);
        when(userService.getById(eq(2L))).thenReturn(booker);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDtoOut savedBooking = bookingService.create(bookingDtoIn, booker.getId());

        Assertions.assertNotNull(savedBooking);
        Assertions.assertEquals(booking.getId(), savedBooking.getId());
        Assertions.assertEquals(booking.getStart(), savedBooking.getStart());
        Assertions.assertEquals(booking.getEnd(), savedBooking.getEnd());
        Assertions.assertEquals(booking.getBooker().getId(), savedBooking.getBooker());
        Assertions.assertEquals(booking.getStatus(), savedBooking.getStatus());
    }

    @Test
    void createTestBookerIsOwner() {
        when(itemService.getItemById(eq(1L))).thenReturn(item);
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(bookingDtoIn, owner.getId()));

        Assertions.assertEquals("Пользователь, создавший запрос, является владельцем данной вещи",
                exception.getMessage());
    }

    @Test
    void createTestItemUnavailable() {
        when(itemService.getItemById(eq(1L))).thenReturn(item);
        item.setAvailable(false);

        Exception exception = Assertions.assertThrows(BookingException.class,
                () -> bookingService.create(bookingDtoIn, booker.getId()));

        Assertions.assertEquals("Вещь не доступна для аренды в данный момент", exception.getMessage());
    }

    @Test
    void updateTestApproved() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDtoOut updatedBooking = bookingService.update(booking.getId(), owner.getId(), true);

        Assertions.assertNotNull(updatedBooking);
        Assertions.assertEquals(booking.getStatus(), Status.APPROVED);
    }

    @Test
    void updateTestRejected() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDtoOut updatedBooking = bookingService.update(booking.getId(), owner.getId(), false);

        Assertions.assertNotNull(updatedBooking);
        Assertions.assertEquals(booking.getStatus(), Status.REJECTED);
    }

    @Test
    void updateTestAlreadyApproved() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Exception exception = Assertions.assertThrows(BookingException.class,
                () -> bookingService.update(booking.getId(), owner.getId(), false));

        Assertions.assertEquals("Бронирование уже подтверждено", exception.getMessage());
    }

    @Test
    void updateTestFromWrongOwner() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.update(booking.getId(), user.getId(), false));

        Assertions.assertEquals("Пользователь с id " + user.getId() + "не является владельцем",
                exception.getMessage());
    }

    @Test
    void getByIdTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDtoOut foundBooking = bookingService.getById(booking.getId(), owner.getId());

        Assertions.assertNotNull(foundBooking);
        Assertions.assertEquals(booking.getId(), foundBooking.getId());
        Assertions.assertEquals(booking.getStart(), foundBooking.getStart());
        Assertions.assertEquals(booking.getEnd(), foundBooking.getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBooking.getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBooking.getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBooking.getStatus());
    }

    @Test
    void getByIdTestWrongUser() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.getById(booking.getId(), user.getId()));

        Assertions.assertEquals("Пользователь не относится к сделке", exception.getMessage());
    }

    @Test
    void getByIdTestBookingNotFound() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.getById(booking.getId(), user.getId()));

        Assertions.assertEquals("Бронирование с id " + booking.getId() + " не найдено", exception.getMessage());
    }

    @Test
    void getByOwnerAll() {
        when(userService.getById(eq(1L))).thenReturn(owner);
        when(bookingRepository.findByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> foundBookings = bookingService.getByOwner(owner.getId(), "ALL", 0, 999);

        Assertions.assertNotNull(foundBookings);
        Assertions.assertEquals(booking.getId(), foundBookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), foundBookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), foundBookings.get(0).getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBookings.get(0).getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBookings.get(0).getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByOwnerCurrent() {
        when(userService.getById(eq(1L))).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class))
        )
                .thenReturn(List.of(booking));

        List<BookingDtoOut> foundBookings = bookingService.getByOwner(owner.getId(), "CURRENT", 0, 999);

        Assertions.assertNotNull(foundBookings);
        Assertions.assertEquals(booking.getId(), foundBookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), foundBookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), foundBookings.get(0).getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBookings.get(0).getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBookings.get(0).getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByOwnerPast() {
        when(userService.getById(eq(1L))).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> foundBookings = bookingService.getByOwner(owner.getId(), "PAST", 0, 999);

        Assertions.assertNotNull(foundBookings);
        Assertions.assertEquals(booking.getId(), foundBookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), foundBookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), foundBookings.get(0).getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBookings.get(0).getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBookings.get(0).getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByOwnerFuture() {
        when(userService.getById(eq(1L))).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> foundBookings = bookingService.getByOwner(owner.getId(), "FUTURE", 0, 999);

        Assertions.assertNotNull(foundBookings);
        Assertions.assertEquals(booking.getId(), foundBookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), foundBookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), foundBookings.get(0).getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBookings.get(0).getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBookings.get(0).getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByOwnerWaiting() {
        when(userService.getById(eq(1L))).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), eq(Status.WAITING), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> foundBookings = bookingService.getByOwner(owner.getId(), "WAITING", 0, 999);

        Assertions.assertNotNull(foundBookings);
        Assertions.assertEquals(booking.getId(), foundBookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), foundBookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), foundBookings.get(0).getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBookings.get(0).getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBookings.get(0).getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByOwnerRejected() {
        when(userService.getById(eq(1L))).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), eq(Status.REJECTED), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> foundBookings = bookingService.getByOwner(owner.getId(), "REJECTED", 0, 999);

        Assertions.assertNotNull(foundBookings);
        Assertions.assertEquals(booking.getId(), foundBookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), foundBookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), foundBookings.get(0).getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBookings.get(0).getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBookings.get(0).getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByOwnerWrongState() {
        when(userService.getById(eq(1L))).thenReturn(owner);

        Exception exception = Assertions.assertThrows(WrongBookingStateException.class,
                () -> bookingService.getByOwner(owner.getId(), "STRANGE_STATE", 0, 999));

        Assertions.assertEquals("Ошибка в параметре состояния", exception.getMessage());
    }

    @Test
    void getByBookerAll() {
        when(userService.getById(eq(2L))).thenReturn(booker);
        when(bookingRepository.findByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> foundBookings = bookingService.getByBooker(booker.getId(), "ALL", 0, 999);

        Assertions.assertNotNull(foundBookings);
        Assertions.assertEquals(booking.getId(), foundBookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), foundBookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), foundBookings.get(0).getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBookings.get(0).getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBookings.get(0).getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByBookerCurrent() {
        when(userService.getById(eq(2L))).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class))
        )
                .thenReturn(List.of(booking));

        List<BookingDtoOut> foundBookings = bookingService.getByBooker(booker.getId(), "CURRENT", 0, 999);

        Assertions.assertNotNull(foundBookings);
        Assertions.assertEquals(booking.getId(), foundBookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), foundBookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), foundBookings.get(0).getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBookings.get(0).getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBookings.get(0).getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByBookerPast() {
        when(userService.getById(eq(2L))).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> foundBookings = bookingService.getByBooker(booker.getId(), "PAST", 0, 999);

        Assertions.assertNotNull(foundBookings);
        Assertions.assertEquals(booking.getId(), foundBookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), foundBookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), foundBookings.get(0).getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBookings.get(0).getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBookings.get(0).getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByBookerFuture() {
        when(userService.getById(eq(2L))).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> foundBookings = bookingService.getByBooker(booker.getId(), "FUTURE", 0, 999);

        Assertions.assertNotNull(foundBookings);
        Assertions.assertEquals(booking.getId(), foundBookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), foundBookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), foundBookings.get(0).getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBookings.get(0).getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBookings.get(0).getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByBookerWaiting() {
        when(userService.getById(eq(2L))).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), eq(Status.WAITING), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> foundBookings = bookingService.getByBooker(booker.getId(), "WAITING", 0, 999);

        Assertions.assertNotNull(foundBookings);
        Assertions.assertEquals(booking.getId(), foundBookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), foundBookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), foundBookings.get(0).getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBookings.get(0).getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBookings.get(0).getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByBookerRejected() {
        when(userService.getById(eq(2L))).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), eq(Status.REJECTED), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> foundBookings = bookingService.getByBooker(booker.getId(), "REJECTED", 0, 999);

        Assertions.assertNotNull(foundBookings);
        Assertions.assertEquals(booking.getId(), foundBookings.get(0).getId());
        Assertions.assertEquals(booking.getStart(), foundBookings.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), foundBookings.get(0).getEnd());
        Assertions.assertEquals(booking.getItem().getId(), foundBookings.get(0).getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), foundBookings.get(0).getBooker());
        Assertions.assertEquals(booking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByBookerWrongState() {
        when(userService.getById(eq(2L))).thenReturn(booker);

        Exception exception = Assertions.assertThrows(WrongBookingStateException.class,
                () -> bookingService.getByBooker(booker.getId(), "STRANGE_STATE", 0, 999));

        Assertions.assertEquals("Ошибка в параметре состояния", exception.getMessage());
    }

}
