package ru.yandex.practicum.filmorate.controllers;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    private int id = 0;

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        logRequests("POST", "/films", film.toString());
        Film film1 = checkOnValidation(film);
        if (!films.containsKey(film1.getId())) {
            films.put(film1.getId(), film1);
        } else {
            throw new ValidationException("Кино уже добавлено!");
        }
        return film;
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
        logRequests("PUT", "/films", film.toString());
        Film film1 = checkOnValidation(film);
        films.put(film1.getId(), film1);
        return film;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        logRequests("GET", "/films", "no body");
        return films.values();
    }

    private Film checkOnValidation(Film film) {
        if (film.getId() == 0) {
            idGenerate();
            film.setId(id);
        }
        if (film.getId() < 0) {
            throw new ValidationException("id должно быть положительным!");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate()
                .isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше  28 декабря 1895 года!");
        }
        return film;
    }

    private void idGenerate() {
        id++;
    }

    private void logRequests(String method, String uri, String body) {
        log.info("Получен запрос: '{} {}'. Тело запроса: '{}'", method, uri, body);
    }
}
