package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {

        logRequests(HttpMethod.POST, "/films", film.toString());
        return filmService.postFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {

        logRequests(HttpMethod.PUT, "/films", film.toString());
        return filmService.updateFilm(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable long filmId, @PathVariable long userId) {

        logRequests(HttpMethod.PUT, "/films/" + filmId + "/like/" + userId, "no body");
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable long filmId, @PathVariable long userId) {

        logRequests(HttpMethod.DELETE, "/films/" + filmId + "/like/" + userId, "no body");
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {

        logRequests(HttpMethod.GET, "/films", "no body");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}/likes")//получить список id пользователей, поставивших лайк
    public List<Long> getListOfLikes(@PathVariable long id) {
        logRequests(HttpMethod.GET, "/films/" + id + "/likes", "no body");
        return filmService.getListOfLikes(id);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {

        logRequests(HttpMethod.GET, "/films/" + id, "no body");
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getBestFilms(@RequestParam(defaultValue = "10") @Positive int count) {

        logRequests(HttpMethod.GET, "/films/popular?count=" + count, "no body");
        return filmService.getBestFilms(count);
    }

    private void logRequests(HttpMethod method, String uri, String body) {

        log.info("Получен запрос: '{} {}'. Тело запроса: '{}'", method, uri, body);
    }
}
