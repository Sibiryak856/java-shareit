package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentCreateDto {

    @NotBlank
    private String text;

    private LocalDateTime created;

}
