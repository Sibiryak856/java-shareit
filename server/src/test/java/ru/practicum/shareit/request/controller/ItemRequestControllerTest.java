package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
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

    @Test
    void create_whenRequestIsValid_thenStatusIsCreatedAndReturnSavedRequest() throws Exception {
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

    @Test
    void create_whenUserNotFound_thenStatusIsNotFound() throws Exception {
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

    @Test
    void findAll_whenArgsIsValid_thenStatusIsOkAndReturnListOfRequestDto() throws Exception {
        when(requestService.findAll(anyLong(), any(Pageable.class)))
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

    @Test
    void findAllByUser_whenArgsIsValid_thenStatusIsOkAndReturnListOfRequestDto() throws Exception {
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

    @Test
    void findAllByUser_whenUserNotFound_thenStatusIsNotFound() throws Exception {
        when(requestService.findAllByUser(anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_whenArgsISValid_thenStatusIsOkAndReturnRequestDto() throws Exception {
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

    @Test
    void findById_whenUserOrItemNotFound_thenStatusIsNotFound() throws Exception {
        when(requestService.findById(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/{requestId}", requestDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }
}