package ru.practicum.shareit.user.model.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя пользователя не указано")
    private String name;

    @Email(message = "Введен некорректный email.")
    @NotNull(message = "Email не указан")
    private String email;
}
