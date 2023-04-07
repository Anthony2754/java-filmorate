package ru.yandex.practicum.filmorate.storage.memory.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.RepeatException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long id = 0;

    @Override
    public Film postFilm(Film film) {
        if (films.containsKey(film.getId())) {
            throw new RepeatException("Кино уже добавлено!");
        }

        if (film.getId() == 0) {
            idGenerate();
            film.setId(id);
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilm(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(long id) {
        checkFilm(id);
        return films.get(id);
    }

    @Override
    public Collection<Film> getAllFilms() {

        return films.values();
    }

    private void checkFilm(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException(String.format("Фильм с id %s не найден", id));
        }
    }

    private void idGenerate() {
        id++;
    }
}