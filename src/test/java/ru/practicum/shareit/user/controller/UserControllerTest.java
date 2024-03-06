package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserServiceImpl userService;

    @Autowired
    private MockMvc mvc;

    private UserCreateDto userCreateDto;
    private UserDto userDto;

    private UserDto userUpdatedDto;
    private UserUpdateDto userDtoToUpdate;

    private List<UserDto> users;

    @BeforeEach
    void setUp() {
        userCreateDto = UserCreateDto.builder()
                .name("name")
                .email("name@email.ru")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("name@email.ru")
                .build();

        userDtoToUpdate = UserUpdateDto.builder()
                .name("updateName")
                .email("updateName@email.ru")
                .build();

        userUpdatedDto = UserDto.builder()
                .id(1L)
                .name("updateName")
                .email("updateName@email.ru")
                .build();

        users = new ArrayList<>();
    }

    @AfterEach
    void clean() {
        userDtoToUpdate = null;
        userDto = null;
        userCreateDto = null;
        users = null;
    }

    @SneakyThrows
    @Test
    void getAll() {
        users.add(userDto);

        when(userService.getAll())
                .thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(users.size())));

        verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void getUser() {
        when(userService.getUser(userDto.getId()))
                .thenReturn(userDto);

        mvc.perform(get("/users/{id}", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService).getUser(userDto.getId());
    }

    @SneakyThrows
    @Test
    void create_whenUserIsValid_thenStatusIsCreatedAndReturnSavedUser() {
        when(userService.create(any(UserCreateDto.class)))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(String.valueOf(mapper.writeValueAsString(userCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService).create(userCreateDto);
    }

    @SneakyThrows
    @Test
    void create_whenUserEmailIsNotValid_thenReturnBadRequest() {
        userCreateDto.setEmail("e.ru");
        mvc.perform(post("/users")
                        .content(String.valueOf(mapper.writeValueAsString(userCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(userCreateDto);
    }

    @SneakyThrows
    @Test
    void create_whenUserEmailIsNull_thenReturnBadRequest() {
        userCreateDto.setEmail(null);
        mvc.perform(post("/users")
                        .content(String.valueOf(mapper.writeValueAsString(userCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(userCreateDto);
    }

    @SneakyThrows
    @Test
    void create_whenUserNameIsBlank_thenReturnBadRequest() {
        userCreateDto.setName("");
        mvc.perform(post("/users")
                        .content(String.valueOf(mapper.writeValueAsString(userCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(userCreateDto);
    }

    @SneakyThrows
    @Test
    void update_whenUserIsValid_thenStatusIsOkAndReturnUpdatedUser() {
        when(userService.update(userDto.getId(), userDtoToUpdate))
                .thenReturn(userUpdatedDto);

        mvc.perform(patch("/users/{id}", userDto.getId())
                        .content(String.valueOf(mapper.writeValueAsString(userDtoToUpdate)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userUpdatedDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userUpdatedDto.getName())))
                .andExpect(jsonPath("$.email", is(userUpdatedDto.getEmail())));

        verify(userService).update(userDto.getId(), userDtoToUpdate);
    }

    @SneakyThrows
    @Test
    void update_whenUserEmailIsNotValid_thenBadRequest() {
        userDtoToUpdate.setEmail("e.ru");

        mvc.perform(patch("/users/{id}", userDto.getId())
                        .content(String.valueOf(mapper.writeValueAsString(userDtoToUpdate)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).update(userDto.getId(), userDtoToUpdate);
    }

    @SneakyThrows
    @Test
    void delete() {
        mvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userDto.getId()))
                .andExpect(status().isNoContent());

        verify(userService).delete(userDto.getId());
    }
}