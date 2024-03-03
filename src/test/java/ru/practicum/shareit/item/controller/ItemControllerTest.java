package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemServiceImpl itemService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;
    private ItemDto updatedItemDto;
    private List<ItemDto> items;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(TRUE)
                .owner(User.builder().id(1L).build())
                .lastBooking(null)
                .nextBooking(null)
                .requestId(null)
                .comments(Collections.emptyList())
                .build();
        itemCreateDto = ItemCreateDto.builder()
                .name("name")
                .description("description")
                .available(TRUE)
                .build();
        itemUpdateDto = ItemUpdateDto.builder()
                .id(1L)
                .name("newName")
                .description("newDescription")
                .available(TRUE)
                .build();
        updatedItemDto = ItemDto.builder()
                .id(1L)
                .name("newName")
                .description("newDescription")
                .available(TRUE)
                .build();
        items = new ArrayList<>();
    }

    @AfterEach
    void clean() {
        itemDto = null;
        itemCreateDto = null;
        itemUpdateDto = null;
        updatedItemDto = null;
        items = null;
    }

    @SneakyThrows
    @Test
    void getAllByUser_whenUsersExist_thenStatusOkAndReturnList() {
        Long ownerId = 1L;
        int from = 5;
        int size = 10;

        when(itemService.getAllByOwner(ownerId, from, size))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(itemService).getAllByOwner(ownerId, from, size);
    }

    @SneakyThrows
    @Test
    void getAllByUser_whenRequestParamFromFalse_thenBadRequest() {
        Long ownerId = 1L;
        int from = -1;
        int size = 10;
        when(itemService.getAllByOwner(ownerId, from, size))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllByOwner(ownerId, from, size);
    }

    @SneakyThrows
    @Test
    void getAllByUser_whenRequestSizeFromFalse_thenBadRequest() {
        Long ownerId = 1L;
        int from = 10;
        int size = 0;
        when(itemService.getAllByOwner(ownerId, from, size))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllByOwner(ownerId, from, size);
    }

    @SneakyThrows
    @Test
    void getItem() {
        Long id = itemDto.getId();

        when(itemService.getItem(itemDto.getId(), 1L))
                .thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService).getItem(itemDto.getId(), 1L);
    }

    @SneakyThrows
    @Test
    void create_whenItemIsValid_thenStatusIsCreatedAndReturnSavedItem() {
        Long ownerId = 1L;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(new User()));

        when(itemService.create(itemCreateDto, ownerId))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService).create(itemCreateDto, ownerId);
    }

    @SneakyThrows
    @Test
    void create_whenItemIsNotValid_thenStatusIsBadRequest() {
        Long ownerId = 1L;
        itemCreateDto.setName("");

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(itemCreateDto, ownerId);
    }

    @SneakyThrows
    @Test
    void update_whenItemIsValid_thenStatusIsOkAndReturnUpdatedItem() {
        Long ownerId = 1L;
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(new User()));

        when(itemService.update(itemDto.getId(), ownerId, itemUpdateDto))
                .thenReturn(updatedItemDto);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemUpdateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updatedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItemDto.getAvailable())));

        verify(itemService).update(1L, 1L, itemUpdateDto);
    }

    @SneakyThrows
    @Test
    void update_whenFieldsEmpty_thenStatusIsOkAndReturnUpdatedItem() {
        Long ownerId = 1L;
        itemUpdateDto.setName(null);
        itemUpdateDto.setDescription(null);
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(new User()));

        when(itemService.update(itemDto.getId(), ownerId, itemUpdateDto))
                .thenReturn(updatedItemDto);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemUpdateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updatedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItemDto.getAvailable())));

        verify(itemService).update(1L, 1L, itemUpdateDto);
    }

    @SneakyThrows
    @Test
    void delete() {
        Long ownerId = 1L;
        mvc.perform(MockMvcRequestBuilders.delete("/items/{itemId}", itemDto.getId())
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNoContent());

        verify(itemService).delete(itemDto.getId(), ownerId);
    }

    @SneakyThrows
    @Test
    void search() {
        Long ownerId = 1L;
        int from = 5;
        int size = 10;
        items.add(itemDto);
        when(itemService.getSearcherItems(String.valueOf(""), from, size))
                .thenReturn(items);

        String itemsDTO = mvc.perform(get("/items/search")
                        .param("text", "")
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(mapper.writeValueAsString(items)).isEqualTo(itemsDTO);
        verify(itemService).getSearcherItems(String.valueOf(""), from, size);
    }

    /*@Test
    void testCreate() {
    }*/
}