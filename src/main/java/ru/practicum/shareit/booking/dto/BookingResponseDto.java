package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingResponseDto {

    Long id;

    LocalDateTime startTime;

    LocalDateTime endTime;

    BookingStatus status;

    UserDto booker;

    ItemDto item;

    @Data
    public static class UserDto {
        Long id;
    }

    @Data
    public static class ItemDto {
        Long id;

        String name;
    }
}
