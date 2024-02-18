package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingCreateDto bookingDto, long userId);

    BookingResponseDto update(Long bookingId, long userId, Boolean approved);

    BookingResponseDto getBooking(Long bookingId, long userId);

    List<BookingResponseDto> getAllByOwnerQuery(long userId, String state);

    List<BookingResponseDto> getAllByUserQuery(long userId, String state);
}
