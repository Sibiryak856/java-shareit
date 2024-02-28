package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.booking.controller.BookingController;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {


    @BeforeEach
    void setUp() {
    }

    @Test
    void create() {
    }

    @Test
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
    }
}