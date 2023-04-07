package ru.practicum.shareit.booking.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

@JsonTest
public class BookingDtoTest {

    private static final LocalDateTime START =
            LocalDateTime.of(2023, 2, 3, 9, 0, 1);

    private static final LocalDateTime END =
            LocalDateTime.of(2033, 2, 4, 9, 0, 1);

    @Autowired
    private JacksonTester<BookingDto> jacksonTester;

    @Test
    void bookingDtoTest() throws Exception {
        BookingDto bookingDtoOut = new BookingDto(
                1L,
                START,
                END,
                null
        );

        JsonContent<BookingDto> result = jacksonTester.write(bookingDtoOut);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingDtoOut.getId().intValue());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDtoOut.getStart().toString());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDtoOut.getEnd().toString());
    }

}
