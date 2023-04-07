package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {

    Film postFilm(Film film);
    Film updateFilm(Film film);
    Film getFilmById(long id);
    Collection<Film> getAllFilms();
}

