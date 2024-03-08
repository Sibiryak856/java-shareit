package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentCreateDtoTest {

    @Autowired
    private JacksonTester<CommentCreateDto> json;

    @Test
    void testCommentDto() throws IOException {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("text")
                .build();

        JsonContent<CommentCreateDto> result = json.write(commentCreateDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

}