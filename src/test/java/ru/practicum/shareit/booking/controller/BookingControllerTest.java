package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingServiceImpl bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;
    private BookingCreateDto bookingCreateDto;
    private Booking booking;
    private User booker;
    private Item item;


    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(1).withNano(0);
        LocalDateTime end = start.plusMinutes(10);
        bookingCreateDto = BookingCreateDto.builder()
                .itemId(1L)
                .startTime(start)
                .endTime(end)
                .build();
        booker = User.builder()
                .id(1L)
                .build();
        item = Item.builder()
                .id(1L)
                .build();
        bookingDto = BookingDto.builder()
                .id(1L)
                .startTime(start)
                .endTime(end)
                .status(BookingStatus.WAITING)
                .build();
        booking = Booking.builder()
                .id(1L)
                .startTime(start)
                .endTime(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING).build();
    }

    @SneakyThrows //don't work
    @Test
    void create_whenBookingIsValid_thenStatusISCreatedAndReturnBookingDto() {
        when(bookingService.create(any(BookingCreateDto.class), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(bookingCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStartTime().getYear()))) // try this
                .andExpect(jsonPath("$.end", is(bookingDto.getEndTime())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

        verify(bookingService).create(any(BookingCreateDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void create_whenStartTimeInPast_thenStatusIBadRequest() {
        bookingCreateDto.setStartTime(LocalDateTime.now().minusMinutes(3));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(bookingCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(any(BookingCreateDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void create_whenEndBeforeStart_thenStatusIBadRequest() {
        bookingCreateDto.setEndTime(LocalDateTime.now().minusMinutes(3));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(bookingCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(any(BookingCreateDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void create_whenItemIdIsNull_thenStatusIBadRequest() {
        bookingCreateDto.setItemId(null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(bookingCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(any(BookingCreateDto.class), anyLong());
    }

    /*@Test
    void update() {
    }

    @Test
    void getById() {
    }

    @Test
    void getAllByUserQuery() {
    }

    @Test
    void getAllByOwnerQuery() {
    }*/
}