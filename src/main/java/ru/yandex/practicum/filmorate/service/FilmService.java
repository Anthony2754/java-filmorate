package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dal.LikesStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikesStorage likesStorage;

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

    public void addLike(long filmId, long userId) {

        boolean add;
        filmStorage.getFilmById(filmId);
        userService.getUserById(userId);
        add = likesStorage.addLike(filmId, userId);

        saveInLog(HttpMethod.PUT, "/films/" + filmId + "/like/" + userId, ((Boolean) add).toString());
    }

    public void deleteLike(long filmId, long userId) {

        boolean delete;
        filmStorage.getFilmById(filmId);
        userService.getUserById(userId);
        delete = likesStorage.deleteLike(filmId, userId);

        if (!delete) {
            throw new NotFoundException(String.format("Пользователь с id %s не ставил лайк фильму с id %s",
                    userId, filmId));
        }

        saveInLog(HttpMethod.DELETE, "/films/" + filmId + "/like/" + userId, ((Boolean) delete).toString());
    }

    public List<Long> getListOfLikes(long id) {
        filmStorage.getFilmById(id);
        List<Long> listLike = likesStorage.getLikesList(id);
        saveInLog(HttpMethod.GET, "/films/" + id + "/likes", listLike.toString());
        return listLike;
    }

    public List<Film> getBestFilms(int count) {

        List<Film> bestFilms = likesStorage.getBestFilms(count).stream()
                .map(filmStorage::getFilmById)
                .collect(Collectors.toList());

        saveInLog(HttpMethod.GET, "/films/popular?count=" + count, bestFilms.toString());
        return bestFilms;
    }

    private Film checkOnValidation(Film film) {

        if (film.getReleaseDate() != null && film.getReleaseDate()
                .isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше  28 декабря 1895 года!");
        }
        return film;
    }

    private void saveInLog(HttpMethod method, String uri, String storage) {
        log.info("Получен запрос: '{} {}'. В хранилище: '{}'", method, uri, storage);
    }
}

