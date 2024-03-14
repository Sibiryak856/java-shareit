package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingCreateDto bookingDto, long userId);

    BookingDto update(Long bookingId, long userId, Boolean approved);

    BookingDto getBooking(Long bookingId, long userId);

    List<BookingDto> getAllByOwnerQuery(long userId, BookingState state, Pageable pageable);

    List<BookingDto> getAllByUserQuery(long userId, BookingState state, Pageable pageable);
}
