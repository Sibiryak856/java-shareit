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
class ItemCreateDtoTest {

    @Autowired
    private JacksonTester<ItemCreateDto> json;

    @Test
    void testItemDto() throws IOException {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("item")
                .description("description")
                .available(TRUE)
                .requestId(1L)
                .build();

        JsonContent<ItemCreateDto> result = json.write(itemCreateDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemCreateDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemCreateDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemCreateDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

}