package ru.practicum.shareit.user.dto;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {

    private Long id;
    private String name;

    @Email(message = "Некорректный адрес электронной почты")
    @NotNull
    private String email;
}
