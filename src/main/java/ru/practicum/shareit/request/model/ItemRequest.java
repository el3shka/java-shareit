package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;



@Entity
@ToString
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotBlank
    @Column(nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requestor;
}
