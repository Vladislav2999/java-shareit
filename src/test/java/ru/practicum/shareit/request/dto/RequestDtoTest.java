package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoTest {

    private static final LocalDateTime CREATED = LocalDateTime.of(2023, 2, 3, 9, 0, 1);

    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @Test
    public void requestDtoJsonTest() throws IOException {

        ItemRequestDto itemRequestDto
                = new ItemRequestDto(1L, "description", CREATED, null);

        JsonContent<ItemRequestDto> result = jacksonTester.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestDto.getCreated().toString());
    }
}
