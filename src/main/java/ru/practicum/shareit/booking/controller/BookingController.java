package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<BookingDtoOut> create(@RequestHeader(SHARER_USER_ID) Long userId,
                                                @Validated @RequestBody BookingDtoOut bookingDto) {
        return ResponseEntity.ok().body(bookingService.create(bookingDto, userId));

    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOut> update(@RequestParam("approved") Boolean approved,
                                                @PathVariable("bookingId") Long bookingId,
                                                @RequestHeader(SHARER_USER_ID) Long userId) {


        return ResponseEntity.ok().body(bookingService.update(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOut> getById(@PathVariable("bookingId") Long bookingId,
                                                 @RequestHeader(SHARER_USER_ID) Long userId) {
        return ResponseEntity.ok().body(bookingService.getById(bookingId, userId));

    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoOut>> getByOwner(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                          @RequestHeader(SHARER_USER_ID) Long userId,
                                                          @RequestParam(value = "from", defaultValue = "0")
                                                              @PositiveOrZero int from,
                                                          @RequestParam(value = "size", defaultValue = "10")
                                                              @Positive int size) {
        return ResponseEntity.ok().body(bookingService.getByOwner(userId, state, from, size));

    }

    @GetMapping
    public ResponseEntity<List<BookingDtoOut>> getByBooker(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                           @RequestHeader(SHARER_USER_ID) Long userId,
                                                           @RequestParam(value = "from", defaultValue = "0")
                                                               @PositiveOrZero int from,
                                                           @RequestParam(value = "size", defaultValue = "10")
                                                               @Positive int size) {
        return ResponseEntity.ok().body(bookingService.getByBooker(userId, state, from, size));

    }

}
