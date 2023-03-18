package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private long id = 0;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film postFilm(Film film) {

        Film filmInStorage = filmStorage.postFilm(checkOnValidation(film));

        saveInLog(HttpMethod.POST, "/films", filmInStorage.toString());
        return filmInStorage;
    }

    public Film updateFilm(Film film) {

        Film filmInStorage = filmStorage.updateFilm(checkOnValidation(film));

        saveInLog(HttpMethod.PUT, "/films", filmInStorage.toString());
        return filmInStorage;
    }

    public Film getFilmById(long id) {

        Film filmInStorage = filmStorage.getFilmById(id);

        saveInLog(HttpMethod.GET, "/films/" + id, filmInStorage.toString());
        return filmInStorage;
    }

    public Collection<Film> getAllFilms() {

        Collection<Film> filmsInStorage = filmStorage.getAllFilms();

        saveInLog(HttpMethod.GET, "/films", filmsInStorage.toString());
        return filmsInStorage;
    }

    public Film addLike(long filmId, long userId) {

        Film film = filmStorage.getFilmById(filmId);
        userService.getUserById(userId);
        film.getLikes().add(userId);

        saveInLog(HttpMethod.PUT, "/films/" + filmId + "/like/" + userId, film.toString());
        return film;
    }

    public Film deleteLike(long filmId, long userId) {

        Film film = filmStorage.getFilmById(filmId);
        userService.getUserById(userId);

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException(String.format("Пользователь с id %s не ставил лайк фильму с id %s",
                    userId, filmId));
        }

        film.getLikes().remove(userId);

        saveInLog(HttpMethod.DELETE, "/films/" + filmId + "/like/" + userId, film.toString());
        return film;
    }

    public List<Film> getBestFilms(int count) {

        List<Film> bestFilms = filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(f -> f.getLikes().size(), (f1, f2) -> f2 - f1))
                .limit(count)
                .collect(Collectors.toList());

        saveInLog(HttpMethod.GET, "/films/popular?count=" + count, bestFilms.toString());
        return bestFilms;
    }

    private void idGenerate() {
        id++;
    }

    private Film checkOnValidation(Film film) {

        if (film.getReleaseDate() != null && film.getReleaseDate()
                .isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше  28 декабря 1895 года!");
        }

        if (film.getId() == 0) {
            idGenerate();
            film.setId(id);
        }
        return film;
    }

    private void saveInLog(HttpMethod method, String uri, String storage) {
        log.info("Получен запрос: '{} {}'. В хранилище: '{}'", method, uri, storage);
    }
}

