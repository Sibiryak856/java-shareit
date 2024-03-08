package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

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
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @Valid @RequestBody BookingCreateDto bookingDto) {
        log.info("Request received: POST /bookings: {}", bookingDto);
        BookingDto createdBooking = bookingService.create(bookingDto, userId);
        log.info("Request POST /bookings processed: booking={} is created", createdBooking);
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable Long bookingId,
                             @RequestParam Boolean approved
    ) {
        log.info("Request received: PATCH /bookings: id={}", bookingId);
        BookingDto updatedBooking = bookingService.update(bookingId, userId, approved);
        log.info("Request PATCH /bookings processed: booking={} is created", updatedBooking);
        return updatedBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable Long bookingId
                              ) {
        log.info("Request received: GET /bookings/id={}", bookingId);
        BookingDto booking = bookingService.getBooking(bookingId, userId);
        log.info("Request GET /bookings/id processed: {}", booking);
        return booking;
    }

    @GetMapping
    public List<BookingDto> getAllByUserQuery(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @Min(0) @RequestParam(value = "from", defaultValue = "0") int offset,
            @Min(1) @RequestParam(value = "size", defaultValue = "10") int limit
    ) {
        log.debug("Request received: GET /bookings");
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "startTime"));
        List<BookingDto> searchedBookings = bookingService.getAllByUserQuery(userId, bookingState, pageable);
        log.debug("Request GET /bookings processed: searchedBookings: {}", searchedBookings);
        return searchedBookings;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwnerQuery(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @Min(0) @RequestParam(value = "from", defaultValue = "0") int offset,
            @Min(1) @RequestParam(value = "size", defaultValue = "10") int limit
    ) {
        log.debug("Request received: GET /bookings/owner");
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "startTime"));
        List<BookingDto> searchedBookings = bookingService.getAllByOwnerQuery(userId, bookingState, pageable);
        log.debug("Request GET /bookings/owner processed: searchedBookings: {}", searchedBookings);
        return searchedBookings;
    }
}
