package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestCreateDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestClient requestClient;

    @Autowired
    private MockMvc mvc;

    private RequestCreateDto requestCreateDto;

    @BeforeEach
    void setUp() {
        requestCreateDto = new RequestCreateDto("description");
    }

    @AfterEach
    void clean() {
        requestCreateDto = null;
    }

    @Test
    void create_whenRequestIsNotValid_thenStatusIsBadRequest() throws Exception {
        requestCreateDto.setDescription("");

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(requestCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).create(anyLong(), any(RequestCreateDto.class));
    }

    @Test
    void findAll_whenParamFromIsNotValid_thenStatusIsBadRequest() throws Exception {
        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).findAll(anyLong(), anyInt(), anyInt());
    }

    @Test
    void findAll_whenParamSizeIsNotValid_thenStatusIsBadRequest() throws Exception {
        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).findAll(anyLong(), anyInt(), anyInt());
    }
}