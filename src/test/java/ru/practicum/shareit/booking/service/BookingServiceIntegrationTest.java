package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;
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
public class BookingServiceIntegrationTest {

    private static final LocalDateTime START = LocalDateTime.of(2022, 2, 3, 9, 0);

    private static final LocalDateTime END = LocalDateTime.of(2022, 2, 4, 9, 0);

    private final BookingService bookingService;

    private final UserService userService;

    private final ItemService itemService;

    private BookingDtoIn bookingDtoIn;

    private ItemDtoIn itemDtoIn;

    private User owner;

    private User booker;

    @BeforeEach
    void beforeEach() {
        owner = new User(1L, "owner", "owner@mail.ru");
        booker = new User(2L, "booker", "booker@mail.ru");
        itemDtoIn = new ItemDtoIn(null, "name", "description", true, null);
        bookingDtoIn = new BookingDtoIn(1L, START, END, null);
    }

    @Test
    void createTest() {
        User savedOwner = userService.create(owner);
        User savedBooker = userService.create(booker);
        ItemDtoOut savedItem = itemService.create(itemDtoIn, savedOwner.getId());
        bookingDtoIn.setItemId(savedItem.getId());
        BookingDtoOut savedBooking = bookingService.create(bookingDtoIn, savedBooker.getId());
        BookingDtoOut foundBooking = bookingService.getById(savedBooking.getId(), savedOwner.getId());

        assertNotNull(savedBooking);
        assertEquals(foundBooking.getId(), savedBooking.getId());
        assertEquals(foundBooking.getStart(), savedBooking.getStart());
        assertEquals(foundBooking.getEnd(), savedBooking.getEnd());
        assertEquals(foundBooking.getItem().getId(), savedBooking.getItem().getId());
        assertEquals(foundBooking.getStatus(), Status.WAITING);

    }

    @Test
    void updateTest() {
        User savedOwner = userService.create(owner);
        User savedBooker = userService.create(booker);
        ItemDtoOut savedItem = itemService.create(itemDtoIn, savedOwner.getId());
        bookingDtoIn.setItemId(savedItem.getId());
        BookingDtoOut savedBooking = bookingService.create(bookingDtoIn, savedBooker.getId());
        BookingDtoOut updatedBooking = bookingService.update(savedBooking.getId(), savedOwner.getId(), true);
        BookingDtoOut foundBooking = bookingService.getById(updatedBooking.getId(), savedOwner.getId());

        assertNotNull(updatedBooking);
        assertEquals(savedBooking.getId(), foundBooking.getId());
        assertEquals(savedBooking.getStart(), foundBooking.getStart());
        assertEquals(savedBooking.getEnd(), foundBooking.getEnd());
        assertEquals(savedBooking.getItem().getId(), foundBooking.getItem().getId());
        assertEquals(Status.APPROVED, foundBooking.getStatus());
    }

    @Test
    void getByOwnerTest() {
        User savedOwner = userService.create(owner);
        User savedBooker = userService.create(booker);
        ItemDtoOut savedItem = itemService.create(itemDtoIn, savedOwner.getId());
        bookingDtoIn.setItemId(savedItem.getId());
        BookingDtoOut savedBooking = bookingService.create(bookingDtoIn, savedBooker.getId());
        List<BookingDtoOut> foundBookings = bookingService.getByOwner(savedOwner.getId(), "PAST", 0, 99);

        assertNotNull(foundBookings);
        assertEquals(1, foundBookings.size());
        assertEquals(savedBooking.getId(), foundBookings.get(0).getId());
        assertEquals(savedBooking.getStart(), foundBookings.get(0).getStart());
        assertEquals(savedBooking.getEnd(), foundBookings.get(0).getEnd());
        assertEquals(savedBooking.getItem().getId(), foundBookings.get(0).getItem().getId());
        assertEquals(savedBooking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByBookerTest() {
        User savedOwner = userService.create(owner);
        User savedBooker = userService.create(booker);
        ItemDtoOut savedItem = itemService.create(itemDtoIn, savedOwner.getId());
        bookingDtoIn.setItemId(savedItem.getId());
        BookingDtoOut savedBooking = bookingService.create(bookingDtoIn, savedBooker.getId());
        List<BookingDtoOut> foundBookings = bookingService.getByBooker(savedBooker.getId(), "PAST", 0, 99);

        assertNotNull(foundBookings);
        assertEquals(1, foundBookings.size());
        assertEquals(savedBooking.getId(), foundBookings.get(0).getId());
        assertEquals(savedBooking.getStart(), foundBookings.get(0).getStart());
        assertEquals(savedBooking.getEnd(), foundBookings.get(0).getEnd());
        assertEquals(savedBooking.getItem().getId(), foundBookings.get(0).getItem().getId());
        assertEquals(savedBooking.getStatus(), foundBookings.get(0).getStatus());
    }

    @Test
    void getByIdTest() {
        User savedOwner = userService.create(owner);
        User savedBooker = userService.create(booker);
        ItemDtoOut savedItem = itemService.create(itemDtoIn, savedOwner.getId());
        bookingDtoIn.setItemId(savedItem.getId());
        BookingDtoOut savedBooking = bookingService.create(bookingDtoIn, savedBooker.getId());
        BookingDtoOut foundBooking = bookingService.getById(savedBooking.getId(), savedBooker.getId());

        assertNotNull(foundBooking);
        assertEquals(savedBooking.getId(), foundBooking.getId());
        assertEquals(savedBooking.getStart(), foundBooking.getStart());
        assertEquals(savedBooking.getEnd(), foundBooking.getEnd());
        assertEquals(savedBooking.getItem().getId(), foundBooking.getItem().getId());
        assertEquals(Status.WAITING, foundBooking.getStatus());
    }
}
