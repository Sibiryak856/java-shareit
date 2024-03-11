package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private Long requestId;

    private List<CommentDto> comments;

    @Data
    @Builder
    public static class BookingDto {
        Long id;
        Long bookerId;
    }
}
