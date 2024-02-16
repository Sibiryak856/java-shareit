package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

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
    public BookingResponseDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable Long bookingId,
                             @PathVariable Boolean approved
                             ) {
        log.info("Request received: PATCH /bookings: id={}", bookingId);
        BookingResponseDto createdBooking = bookingService.update(bookingId, userId, approved);
        log.info("Request PATCH /bookings processed: booking={} is created", createdBooking);
        return null;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable Long bookingId
                              ) {
        log.info("Request received: GET /bookings/id={}", bookingId);
        BookingResponseDto booking = bookingService.getBooking(bookingId, userId);
        log.info("Request GET /bookings/id processed: {}", booking);
        return null;
    }

    @GetMapping
    public List<BookingResponseDto> getAllByUserQuery(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.debug("Request received: GET /bookings");
        List<BookingResponseDto> searchedBookings = bookingService.getAllByUserQuery(userId, state);
        log.debug("Request GET /bookings processed: searchedBookings: {}", searchedBookings);
        return null;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwnerQuery(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.debug("Request received: GET /bookings/owner");
        List<BookingResponseDto> searchedBookings = bookingService.getAllByOwnerQuery(userId, state);
        log.debug("Request GET /bookings/owner processed: searchedBookings: {}", searchedBookings);
        return searchedBookings;
    }
}
