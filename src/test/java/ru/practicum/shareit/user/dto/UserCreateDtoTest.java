package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserCreateDtoTest {

    @Autowired
    private JacksonTester<UserCreateDto> json;

    @Test
    void testUserCreateDto() throws IOException {
        UserCreateDto userCreateDtoDto = UserCreateDto.builder()
                .name("name")
                .email("name@email.ru")
                .build();

        JsonContent<UserCreateDto> result = json.write(userCreateDtoDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("name@email.ru");
    }

}