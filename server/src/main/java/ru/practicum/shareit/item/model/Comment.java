package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comments")
@EqualsAndHashCode(of = "id")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @NotBlank
    @Size(max = 2000)
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @NotNull
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @NotNull
    private User author;

    @NotNull
    private LocalDateTime created = LocalDateTime.now();
}
