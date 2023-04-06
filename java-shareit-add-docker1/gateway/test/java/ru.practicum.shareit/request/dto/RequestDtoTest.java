package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoTest {

    @Autowired
    private JacksonTester<RequestDto> jacksonTester;

    @Test
    public void requestDtoJsonTest() throws IOException {

        RequestDto itemRequestDto
                = new RequestDto("description");

        JsonContent<RequestDto> result = jacksonTester.write(itemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
    }
}
