package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestServiceImpl requestService;

    @Autowired
    private MockMvc mvc;


    /*@Test
    void create() {
    }

    @Test
    void findAll() {
    }

    @Test
    void findAllByUser() {
    }

    @Test
    void findById() {
    }*/
}