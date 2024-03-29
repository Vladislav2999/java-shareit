package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.mapper.dto.ItemDtoOut;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDtoOut> jacksonTester;

    @Test
    public void itemDtoInJsonTest() throws IOException {

        ItemDtoOut itemDtoOut = new ItemDtoOut(1L, "name", "description", 1L, true, null, null, null, null);

        JsonContent<ItemDtoOut> result = jacksonTester.write(itemDtoOut);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDtoOut.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDtoOut.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDtoOut.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDtoOut.getAvailable());

    }

}
