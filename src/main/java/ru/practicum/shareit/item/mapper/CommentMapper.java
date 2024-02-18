package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    CommentResponseDto toCommentResponseDto(Comment comment);

    Comment toComment(CommentCreateDto commentDto);

    List<CommentResponseDto> toListCommentResponseDto(List<Comment> comments);
}
