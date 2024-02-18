package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {

    private  Long id;

    private String text;

    private String authorName;

    private LocalDateTime created;
}
