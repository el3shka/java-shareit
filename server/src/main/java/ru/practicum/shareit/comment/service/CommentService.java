package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.item.model.Comment;

public interface CommentService {

    Comment addComment(Comment comment, long id, long userId);
}
