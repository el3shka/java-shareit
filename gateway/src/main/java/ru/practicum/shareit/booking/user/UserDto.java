package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.validationMarker.Marker;

@Data
@EqualsAndHashCode(of = "id")
public class UserDto {
    @Null(groups = Marker.OnCreate.class)
    private Long id;
    @NotBlank(message = "name not be blank", groups = Marker.OnCreate.class)
    private String name;
    @Email(message = "incorrect email", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotNull(message = "email not be null", groups = Marker.OnCreate.class)
    private String email;
}
