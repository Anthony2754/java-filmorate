package ru.yandex.practicum.filmorate.model;


import javax.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Film {

    @NotBlank(message = "Укажите название фильма!")
    private final String name;

    private final List<Long> likes;

    private final List<Genre> genres;

    @PositiveOrZero(message = "id должен быть положительным!")
    private long id;

    @Size(max = 200, message = "Максимальная длинна описания 200 символов!")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной!")
    private int duration;

    @NotNull(message = "Mpa = null")
    private MpaModel mpaModel;

    private int rate;
}
