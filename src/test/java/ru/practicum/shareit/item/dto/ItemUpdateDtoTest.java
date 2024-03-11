package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemUpdateDtoTest {

    @Autowired
    private JacksonTester<ItemUpdateDto> json;

    @Test
    void testItemDto() throws IOException {
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(TRUE)
                .build();

        JsonContent<ItemUpdateDto> result = json.write(itemUpdateDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemUpdateDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemUpdateDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemUpdateDto.getAvailable());
    }

}