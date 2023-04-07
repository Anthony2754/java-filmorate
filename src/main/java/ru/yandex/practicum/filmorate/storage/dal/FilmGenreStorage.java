package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

public interface FilmGenreStorage {

    void addGenre(List<Genre> genres, long filmId);

    void deleteGenre(long filmId);

    List<Integer> getGenresList(long id);
}
