package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.RepeatException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    public Film postFilm(Film film) {
        if (films.containsKey(film.getId())) {
            throw new RepeatException("Кино уже добавлено!");
        }
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film film) {
        checkFilm(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    public Film getFilmById(long id) {
        checkFilm(id);
        return films.get(id);
    }

    public Collection<Film> getAllFilms() { return films.values(); }

    private void checkFilm(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException(String.format("Фильм с id %s не найден", id));
        }
    }
}
