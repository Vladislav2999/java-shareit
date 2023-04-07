package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @Test
    public void itemDtoInJsonTest() throws IOException {

        ItemDto itemDtoOut = new ItemDto("name", "description", false, 1L);

        JsonContent<ItemDto> result = jacksonTester.write(itemDtoOut);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDtoOut.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDtoOut.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDtoOut.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDtoOut.getRequestId().intValue());

    }

}
