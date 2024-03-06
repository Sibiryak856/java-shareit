package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

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
        bookingCreateDto = BookingCreateDto.builder()
                .itemId(1L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(10))
                .build();
        booker = User.builder()
                .id(1L)
                .build();
        item = Item.builder()
                .id(1L)
                .build();
        bookingDto = BookingDto.builder()
                .id(1L)
                .startTime(bookingCreateDto.getStartTime())
                .endTime(bookingCreateDto.getEndTime())
                .status(BookingStatus.WAITING)
                .build();
        booking = Booking.builder()
                .id(1L)
                .startTime(bookingCreateDto.getStartTime())
                .endTime(bookingCreateDto.getEndTime())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING).build();
    }

    @Test
    void create() {
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