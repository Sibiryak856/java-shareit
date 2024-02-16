package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface CommentMapper {

    CommentResponseDto toCommentResponseDto(Comment comment);

    Comment toComment(CommentCreateDto commentDto, LocalDateTime created);

    List<CommentResponseDto> toListCommentResponseDto(List<Comment> comments);
}
