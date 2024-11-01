package ru.practicum.shareit.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentDto commentDto);

    List<CommentDto> toCommentDtoList(List<Comment> commentList);

    List<Comment> toCommentList(List<CommentDto> commentDtoList);
}
