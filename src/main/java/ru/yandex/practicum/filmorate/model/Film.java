package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;

    @NotBlank(message = "Укажите название фильма!")
    private final String name;

    @Size(max = 200, message = "Максимальная длинна описания 200 символов!")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной!")
    private int duration;
}
