package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;

    @Email(message = "Введите существующий адрес электронной почты!")
    private final String email;

    @NotBlank(message = "Укажите логин!")
    private final String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем!")
    private LocalDate birthday;
}
