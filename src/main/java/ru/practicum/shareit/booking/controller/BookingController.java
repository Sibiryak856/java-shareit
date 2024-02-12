package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create() {
        return null;
    }

    @PatchMapping
    public BookingDto update() {
        return null;
    }

    @GetMapping("/{bookingId")
    public BookingDto getById() {
        return null;
    }

    @GetMapping
    public List<BookingDto> getByQuery() {
        return null;
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner() {
        return null;
    }
}
