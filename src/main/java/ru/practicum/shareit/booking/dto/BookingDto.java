package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    Long id;

    @JsonProperty("start")
    LocalDateTime startTime;

    @JsonProperty("end")
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
