package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.util.List;

@Data
public class ItemResponseDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentResponseDto> comments;

    @Data
    public static class BookingDto {
        Long id;
        Long bookerId;
    }
}
