package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
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

    @Test
    void getAll() throws Exception {
        users.add(userDto);

        when(userService.getAll())
                .thenReturn(users);

        given(userService.getAll()).willReturn(users);
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(users.size())));
    }

    @Test
    void getUser() throws Exception {
        users.add(userDto);

        when(userService.getUser(userDto.getId()))
                .thenReturn(userDto);

        mvc.perform(get("/users/{id}", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void create() throws Exception {
        when(userService.create(any()))
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
    }

    @Test
    void createWithSameEmail() {
        users.add(userDto);
        UserCreateDto userCreateWrongDto = UserCreateDto.builder()
                .name("newName")
                .email("name@email.com")
                .build();

        when(userService.create(userCreateWrongDto))
                .thenThrow(DataIntegrityViolationException.class);
    }

    @Test
    void updateWithSameEmail() {
        users.add(userDto);
        userUpdatedDto.setId(2L);
        users.add(userUpdatedDto);
        UserUpdateDto userUpdateDtoWrong = UserUpdateDto.builder()
                .name("newName")
                .email("name@email.com")
                .build();

        when(userService.update(2L, userUpdateDtoWrong))
                .thenThrow(DataIntegrityViolationException.class);
    }

    @Test
    void update() throws Exception {
        users.add(userDto);

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
    }

    @Test
    void delete() throws Exception {
        users.add(userDto);

        mvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userDto.getId()))
                .andExpect(status().isNoContent());
    }
}