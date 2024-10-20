package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.markers.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {

    private Long id;
    @NotBlank(groups = {Create.class})
    private String name;
    //@Email(groups = {Create.class, Update.class})
    @Email(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String email;
}