package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingDtoOut {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Item item;

    private Booker booker;

    private Status status;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Item {
        private final long id;
        private final String name;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Booker {
        private final long id;
        private final String name;
    }

}
