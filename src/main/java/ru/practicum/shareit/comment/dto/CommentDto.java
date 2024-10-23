package ru.practicum.shareit.comment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {

    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}