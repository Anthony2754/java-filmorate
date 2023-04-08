package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.log.Logs.saveInLog;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    private final GenreStorage genreStorage;
    private final FilmGenreStorage filmGenreStorage;

    public Collection<Genre> getGenres() {
        Collection<Genre> genreInStorage = genreStorage.getAllGenres();
        saveInLog(HttpMethod.GET, "/genres", genreInStorage.toString());
        return genreInStorage;
    }

    public Genre getGenreById(int id) {
        Genre genreInStorage = genreStorage.getGenreById(id);
        saveInLog(HttpMethod.GET, "/genres/" + id, genreInStorage.toString());
        return genreInStorage;
    }

    public List<Genre> getListOfGenres(long id) {
        return filmGenreStorage.getGenresList(id).stream()
                .map(genreStorage::getGenreById)
                .collect(Collectors.toList());
    }
}

