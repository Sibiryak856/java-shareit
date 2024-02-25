package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserUpdateDtoTest {

    @Autowired
    private JacksonTester<UserUpdateDto> json;

    @Test
    void testUserUpdateDto() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto(
                1L,
                "name",
                "name@email.ru");

        JsonContent<UserUpdateDto> result = json.write(userUpdateDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("name@email.ru");
    }
}