package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {

    @Email(message = "Введите существующий адрес электронной почты!")
    private final String email;

    @NotBlank(message = "Укажите логин!")
    private final String login;

    private final Set<Long> friends = new HashSet<>();

    @PositiveOrZero(message = "id должен быть больше нуля!")
    private long id;

    @NotNull
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем!")
    private LocalDate birthday;
}
