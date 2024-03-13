package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserRequestCreateDto;
import ru.practicum.shareit.user.dto.UserRequestUpdateDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @Autowired
    private MockMvc mvc;

    private UserRequestCreateDto userCreateDto;
    private UserRequestUpdateDto userDtoToUpdate;

    @BeforeEach
    void setUp() {
        userCreateDto = UserRequestCreateDto.builder()
                .name("name")
                .email("name@email.ru")
                .build();
        userDtoToUpdate = UserRequestUpdateDto.builder()
                .name("updateName")
                .email("updateName@email.ru")
                .build();
    }

    @AfterEach
    void clean() {
        userDtoToUpdate = null;
        userCreateDto = null;
    }

    @Test
    void create_whenUserEmailIsNotValid_thenReturnBadRequest() throws Exception {
        userCreateDto.setEmail("e.ru");
        mvc.perform(post("/users")
                        .content(String.valueOf(mapper.writeValueAsString(userCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(UserRequestCreateDto.class));
    }

    @Test
    void create_whenUserEmailIsNull_thenReturnBadRequest() throws Exception {
        userCreateDto.setEmail(null);
        mvc.perform(post("/users")
                        .content(String.valueOf(mapper.writeValueAsString(userCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(UserRequestCreateDto.class));
    }

    @Test
    void create_whenUserNameIsBlank_thenReturnBadRequest() throws Exception {
        userCreateDto.setName("");
        mvc.perform(post("/users")
                        .content(String.valueOf(mapper.writeValueAsString(userCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(UserRequestCreateDto.class));
    }

    @Test
    void update_whenUserEmailIsNotValid_thenBadRequest() throws Exception {
        Long userId = 1L;
        userDtoToUpdate.setEmail("e.ru");

        mvc.perform(patch("/users/{id}", userId)
                        .content(String.valueOf(mapper.writeValueAsString(userDtoToUpdate)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(anyLong(), any(UserRequestUpdateDto.class));
    }
}