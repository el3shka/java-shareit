package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentRetrieveDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.Set;

@Mapper
public interface CommentMapper {

    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    Comment mapToComment(CommentCreateDto dto);

    @Mapping(target = "authorName", source = "author.name")
    CommentRetrieveDto mapToDto(Comment comment);

    Set<CommentRetrieveDto> mapToDto(Set<Comment> comments);
}
