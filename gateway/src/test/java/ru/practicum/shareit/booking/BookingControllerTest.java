package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    private BookItemRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(1).withNano(0);
        LocalDateTime end = start.plusMinutes(10);
        bookingRequestDto = new BookItemRequestDto(1L, start, end);
    }

    @AfterEach
    void clean() {
        bookingRequestDto = null;
    }

    @Test
    void create_whenStartTimeInPast_thenStatusIBadRequest() throws Exception {
        bookingRequestDto.setStart(LocalDateTime.now().minusMinutes(3));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(bookingRequestDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any(BookItemRequestDto.class));
    }

    @Test
    void create_whenEndBeforeStart_thenStatusIBadRequest() throws Exception {
        bookingRequestDto.setEnd(LocalDateTime.now().minusMinutes(3));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(bookingRequestDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any(BookItemRequestDto.class));
    }

    @Test
    void create_whenItemIdIsNull_thenStatusIBadRequest() throws Exception {
        bookingRequestDto.setItemId(null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(bookingRequestDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any(BookItemRequestDto.class));
    }

    @Test
    void getAllByUserQuery_whenStateIsNotValid_thenStatusIsBadRequest() throws Exception {
        Long userId = 1L;

        mvc.perform(get("/bookings", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "smth")
                        .param("from", "5")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsByUser(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    void getAllByUserQuery_whenParamFromIsNotValid_thenStatusIsBadRequest() throws Exception {
        Long userId = 1L;

        mvc.perform(get("/bookings", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsByUser(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    void getAllByUserQuery_whenParamSizeIsNotValid_thenStatusIsBadRequest() throws Exception {
        Long userId = 1L;

        mvc.perform(get("/bookings", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "5")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsByUser(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    void getAllByOwnerQuery_whenStateIsNotValid_thenStatusIsBadRequest() throws Exception {
        Long userId = 1L;

        mvc.perform(get("/bookings/owner", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "smth")
                        .param("from", "5")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsByOwner(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    void getAllByOwnerQuery_whenParamFromIsNotValid_thenStatusIsBadRequest() throws Exception {
        Long userId = 1L;

        mvc.perform(get("/bookings/owner", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsByOwner(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    void getAllByOwnerQuery_whenParamSizeIsNotValid_thenStatusIsBadRequest() throws Exception {
        Long userId = 1L;

        mvc.perform(get("/bookings/owner", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "5")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsByOwner(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }



}