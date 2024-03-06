package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestServiceImpl requestService;

    @MockBean
    private ItemRequestRepository requestRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto requestDto;
    private ItemRequestCreateDto requestCreateDto;
    private ItemRequest request;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("name@email.com")
                .build();
        requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .build();
        request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(requestDto.getCreated())
                .build();
        requestCreateDto = new ItemRequestCreateDto("description");
    }

    @AfterEach
    void clean() {
        requestDto = null;
        requestCreateDto = null;
    }


    @SneakyThrows
    @Test
    void create_whenRequestIsValid_thenStatusIsCreatedAndReturnSavedRequest() {
        Long userId = 1L;
        when(requestService.create(requestCreateDto, userId))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(requestCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));

        verify(requestService).create(requestCreateDto, userId);
    }

    @SneakyThrows
    @Test
    void create_whenRequestIsNotValid_thenStatusIsBadRequest() {
        requestCreateDto.setDescription("");

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(requestCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).create(any(ItemRequestCreateDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void findAll() {
        Long requestorId = 1L;
        int from = 5;
        int size = 10;

        when(requestService.findAll(requestorId, from, size))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/requests/all")
                .header("X-Sharer-User-Id", 1)
                .param("from", "5")
                .param("size", "10"))
                .andExpect(status().isOk());

        verify(requestService).findAll(requestorId, from, size);
    }

    @SneakyThrows
    @Test
    void findAll_whenParamFromIsNotValid_thenStatusIsBadRequest() {
        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).findAll(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAll_whenParamSizeIsNotValid_thenStatusIsBadRequest() {
        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).findAll(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAllByUser() {
        Long requestorId = 1L;

        when(requestService.findAllByUser(requestorId))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestService).findAllByUser(requestorId);
    }

    @SneakyThrows
    @Test
    void findById() {
        Long requestorId = 1L;
        when(requestService.findById(requestDto.getId(), requestorId))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/{requestId}", requestDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));

        verify(requestService).findById(requestDto.getId(), requestorId);
    }
}