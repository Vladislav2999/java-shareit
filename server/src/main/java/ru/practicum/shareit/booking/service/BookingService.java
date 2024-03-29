package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {



    @Transactional
    BookingDtoOut create(BookingDtoOut bookingDtoOut, Long userId);

    BookingDtoOut update(Long bookingId, Long userId, Boolean approved);

    BookingDtoOut getById(Long bookingId, Long userId);

    List<BookingDtoOut> getByBooker(Long bookerId, String state, int from, int size);

    List<BookingDtoOut> getByOwner(Long ownerId, String state, int from, int size);

}