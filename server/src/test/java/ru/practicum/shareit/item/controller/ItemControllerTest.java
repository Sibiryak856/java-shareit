package ru.practicum.shareit.item.controller;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Test
    void getAllByUser_whenUsersExist_thenStatusOkAndReturnList() throws Exception {
        when(itemService.getAllByOwner(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(itemDto));

        String result = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(List.of(itemDto)));
    }

    @Test
    void getAllByUser_whenUserNotFound_thenStatusIsNotFound() throws Exception {
        when(itemService.getAllByOwner(anyLong(), any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 10L)
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItem_whenArgsIsValid_thenStatusIsOkAndReturnItemDto() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemDto);

        String result = mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(itemDto));
    }

    @Test
    void getItem_whenItemNotFound_thenStatusIsNotFound() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_whenItemIsValid_thenStatusIsCreatedAndReturnSavedItem() throws Exception {
        when(itemService.create(any(ItemCreateDto.class), anyLong()))
                .thenReturn(itemDto);

        String result = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(itemDto));
    }

    @Test
    void update_whenItemIsValid_thenStatusIsOkAndReturnUpdatedItem() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemUpdateDto.class)))
                .thenReturn(updatedItemDto);

        String result = mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemUpdateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(updatedItemDto));
    }

    @Test
    void update_whenFieldsEmpty_thenStatusIsOkAndReturnUpdatedItem() throws Exception {
        itemUpdateDto.setName(null);
        itemUpdateDto.setDescription(null);
        when(itemService.update(anyLong(), anyLong(), any(ItemUpdateDto.class)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemUpdateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService).update(anyLong(), anyLong(), any(ItemUpdateDto.class));
    }

    @Test
    void update_whenUserOrItemNotFound_thenStatusIsNotFound() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemUpdateDto.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemUpdateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_whenUserIsNotOwner_thenStatusIsNotFound() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemUpdateDto.class)))
                .thenThrow(NotAccessException.class);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemUpdateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete() throws Exception {
        Long ownerId = 1L;
        mvc.perform(MockMvcRequestBuilders.delete("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNoContent());

        verify(itemService).delete(itemDto.getId(), ownerId);
    }

    @Test
    void search_whenArgsIsValid_thenStatusIsOkAndReturnListOfItemDto() throws Exception {
        items.add(itemDto);
        when(itemService.getSearcherItems(anyString(), any(Pageable.class)))
                .thenReturn(items);

        String result = mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(items));
    }

    @Test
    void createComment_whenCommentIsValid_thenStatusIsOkAndReturnCommentDto() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("name")
                .created(LocalDateTime.now())
                .build();
        when(itemService.create(any(CommentCreateDto.class), anyLong(), anyLong()))
                .thenReturn(commentDto);

        String result = mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(
                                CommentCreateDto.builder()
                                        .text("text")
                                        .build())))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(commentDto));
    }

    @Test
    void createComment_whenUserOrItemNotFound_thenStatusIsNotFound() throws Exception {
        when(itemService.create(any(CommentCreateDto.class), anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(
                                CommentCreateDto.builder()
                                        .text("text")
                                        .build())))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createComment_whenUserHasNotBookings_thenStatusIsBadRequest() throws Exception {
        when(itemService.create(any(CommentCreateDto.class), anyLong(), anyLong()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(
                                CommentCreateDto.builder()
                                        .text("text")
                                        .build())))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}