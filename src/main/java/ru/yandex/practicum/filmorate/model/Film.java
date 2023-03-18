package ru.yandex.practicum.filmorate.model;


import javax.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {

    @NotBlank(message = "Укажите название фильма!")
    private final String name;

    private final Set<Long> likes = new HashSet<>();

    @PositiveOrZero(message = "id должен быть положительным!")
    private long id;

    @Size(max = 200, message = "Максимальная длинна описания 200 символов!")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной!")
    private int duration;
}
