package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestCreateDto;

import java.nio.charset.StandardCharsets;

import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    private ItemRequestCreateDto itemCreateDto;

    @BeforeEach
    void setUp() {
        itemCreateDto = ItemRequestCreateDto.builder()
                .name("name")
                .description("description")
                .available(TRUE)
                .build();
    }

    @AfterEach
    void clean() {
        itemCreateDto = null;
    }

    @Test
    void getAllByUser_whenRequestParamFromFalse_thenBadRequest() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllByUser(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getAllByUser_whenRequestParamSizeFalse_thenBadRequest() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllByUser(anyLong(), anyInt(), anyInt());
    }

    @Test
    void create_whenItemIsNotValid_thenStatusIsBadRequest() throws Exception {
        itemCreateDto.setName("");

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(anyLong(), any(ItemRequestCreateDto.class));
    }

    @Test
    void search_whenRequestParamFromFalse_thenBadRequest() throws Exception {
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "text")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void search_whenRequestSizeFromFalse_thenBadRequest() throws Exception {
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void createComment_whenCommentIsNotValid_thenStatusBadRequest() throws Exception {
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(
                                new CommentRequestDto(""))))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}