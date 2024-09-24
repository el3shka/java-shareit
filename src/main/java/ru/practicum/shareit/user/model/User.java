package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;
    @Email
    private String email;
}
