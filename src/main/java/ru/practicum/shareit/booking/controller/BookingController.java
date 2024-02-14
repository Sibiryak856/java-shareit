package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Slf4j
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookingResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @Valid @RequestBody BookingCreateDto bookingDto) {
        log.info("Request received: POST /bookings: {}", bookingDto);
        BookingResponseDto createdBooking = bookingService.create(bookingDto, userId);
        log.info("Request POST /bookings processed: booking={} is created", createdBooking);
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable Long bookingId,
                             @PathVariable Boolean approved
                             ) {
        log.info("Request received: PATCH /bookings: id={}", bookingId);
        BookingResponseDto createdBooking = bookingService.update(bookingId, userId, approved);
        log.info("Request PATCH /bookings processed: booking={} is created", createdBooking);
        return null;
    }

    @GetMapping("/{bookingId")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable Long bookingId
                              ) {
        log.info("Request received: GET /bookings/id={}", bookingId);
        BookingResponseDto booking = bookingService.getBooking(bookingId, userId);
        log.info("Request GET /bookings/id processed: {}", booking);
        return null;
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwnerQuery(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam String state) {
        log.debug("Request received: GET /bookings/search");
        List<BookingDto> searchedBookings = bookingService.getByOwnerQuery(userId, state);
        log.debug("Request GET /bookings/search processed: searchedItems: {}", searchedBookings);
        return searchedBookings;
    }

    /*@GetMapping("/owner")
    public List<BookingDto> getByOwner() {
        return null;
    }*/
}
