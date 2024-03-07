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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestServiceImpl requestService;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto requestDto;
    private ItemRequestCreateDto requestCreateDto;

    @BeforeEach
    void setUp() {
        LocalDateTime created = LocalDateTime.now();
        requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(created)
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
        when(requestService.create(any(ItemRequestCreateDto.class), anyLong()))
                .thenReturn(requestDto);

        String result = mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(requestCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(requestDto));
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
    void create_whenUserNotFound_thenStatusIsNotFound() {
        when(requestService.create(any(ItemRequestCreateDto.class), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(requestCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void findAll_whenArgsIsValid_thenStatusIsOkAndReturnListOfRequestDto() {
        when(requestService.findAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto));

        String result = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(List.of(requestDto)));
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
    void findAllByUser_whenArgsIsValid_thenStatusIsOkAndReturnListOfRequestDto() {
        when(requestService.findAllByUser(anyLong()))
                .thenReturn(List.of(requestDto));

        String result = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(List.of(requestDto)));
    }

    @SneakyThrows
    @Test
    void findAllByUser_whenUserNotFound_thenStatusIsNotFound() {
        when(requestService.findAllByUser(anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void findById_whenArgsISValid_thenStatusIsOkAndReturnRequestDto() {
        when(requestService.findById(anyLong(), anyLong()))
                .thenReturn(requestDto);

        String result = mvc.perform(get("/requests/{requestId}", requestDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(requestDto));
    }

    @SneakyThrows
    @Test
    void findById_whenUserOrItemNotFound_thenStatusIsNotFound() {
        when(requestService.findById(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/{requestId}", requestDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }
}