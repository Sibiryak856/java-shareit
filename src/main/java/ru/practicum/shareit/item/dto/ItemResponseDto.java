package ru.practicum.shareit.item.dto;

import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class ItemResponseDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;
}
