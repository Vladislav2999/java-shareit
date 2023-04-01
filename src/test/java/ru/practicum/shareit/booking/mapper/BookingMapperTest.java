package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class BookingMapperTest {

    private static final LocalDateTime START = LocalDateTime.of(2023, 2, 3, 9, 0);

    private static final LocalDateTime END = LocalDateTime.of(2033, 2, 4, 9, 0);

    private final User owner = new User(1L, "name", "email@mail.ru");

    private final User booker = new User(2L, "secondName", "secondEmail@mail.ru");

    private final Item item = new Item(1L, "name", "description", true, owner);

    @Autowired
    BookingMapperImpl bookingMapper;

    @Test
    public void toBookingDtoOutTest() {
        Booking booking = new Booking(
                1L,
                START,
                END,
                item,
                booker,
                Status.WAITING
        );

        BookingDtoOut bookingDtoOut = bookingMapper.toBookingDtoOut(booking);

        assertNotNull(bookingDtoOut);
        assertEquals(1, bookingDtoOut.getId());
        assertEquals(START, bookingDtoOut.getStart());
        assertEquals(END, bookingDtoOut.getEnd());
        assertEquals(item.getId(), bookingDtoOut.getItem().getId());
        assertEquals(booker.getId(), bookingDtoOut.getBooker());
        assertEquals(Status.WAITING, bookingDtoOut.getStatus());
    }

    @Test
    public void toBookingTest() {
        BookingDtoIn bookingDtoIn = new BookingDtoIn(null, START, END, null);

        Booking booking =bookingMapper.toBooking(bookingDtoIn);

        assertNotNull(booking);
        assertEquals(START, booking.getStart());
        assertEquals(END, booking.getEnd());
    }
}
