package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;
}
