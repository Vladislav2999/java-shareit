package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exceptionHandler.exception.WrongBookingStateException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {

    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getByBooker(
            @RequestParam(value = "state", defaultValue = "ALL") String stateString,
            @RequestHeader(SHARER_USER_ID) Long userId,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive int size) {
        State state = checkState(stateString);
        log.info("Gateway-BookingController: запрос получения списка аренды по id арендатора - {} и состоянию - {}.",
                userId, stateString);
        return bookingClient.getBookingsByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(
            @RequestParam(value = "state", defaultValue = "ALL") String stateString,
            @RequestHeader(SHARER_USER_ID) Long userId,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive int size) {
        State state = checkState(stateString);
        log.info("Gateway-BookingController: запрос получения списка аренды по id владельца - {} и состоянию - {}.",
                userId, stateString);
        return bookingClient.getBookingsByOwnerAndState(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(
            @PathVariable("bookingId") Long bookingId,
            @RequestHeader(SHARER_USER_ID) Long userId) {
        log.info(
                "Gateway-BookingController: запрос получения информации об аренде по id - {} " +
                        "от пользователя с id - {}.",
                userId,
                bookingId
        );
        return bookingClient.getBooking(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @Validated @RequestBody BookingDto bookingDto) {
        log.info("Gateway-BookingController: запрос создания новой аренды от пользователя с id - {}.", userId);
        return bookingClient.createBooking(userId, bookingDto);
    }


    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(
            @RequestParam("approved") Boolean approved,
            @PathVariable("bookingId") Long bookingId,
            @RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("Gateway-BookingController: запрос обновления статуса аренды с id - {} от пользователя с id - {}.",
                bookingId, userId);
        return bookingClient.updateBookingState(userId, bookingId, approved);
    }

    private State checkState(String state) {
        if (EnumUtils.isValidEnum(State.class, state)) {
            return State.valueOf(state);
        } else {
            throw new WrongBookingStateException("Ошибка в параметре состояния");
        }
    }
}