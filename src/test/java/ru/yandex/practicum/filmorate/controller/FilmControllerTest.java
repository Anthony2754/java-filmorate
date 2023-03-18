package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.RepeatException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


class FilmControllerTest {
    FilmController filmController = new FilmController(
            new FilmService( new InMemoryFilmStorage(), new UserService(
                    new InMemoryUserStorage())));

    @Test
    public void postFilmWithoutIdTest() {
        Film film = Film.builder()
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();

        assertEquals(1, filmController.postFilm(film).getId(), "Не сгенерирован id");
    }

    @Test
    public void postFilmWithOneIdTest() {
        Film film1 = Film.builder()
                .id(1)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        Film film2 = Film.builder()
                .id(1)
                .name("name2")
                .description("description2")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        filmController.postFilm(film1);

        RepeatException exc = Assertions.assertThrows(
                RepeatException.class, () -> filmController.postFilm(film2));
        assertEquals("Кино уже добавлено!", exc.getMessage());
    }

    @Test
    public void postFilmWithoutDescriptionTest() {

        Film film = Film.builder()
                .id(1)
                .name("name1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        filmController.postFilm(film);

        assertTrue(filmController.getAllFilms().contains(film), "Не добавлен фильм без описания");
    }

    @Test
    public void postFilmOlderReleaseDateTest() {
        Film film = Film.builder()
                .id(1)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.of(1020, 11, 12))
                .duration(111)
                .build();

        ValidationException exc = Assertions.assertThrows(
                ValidationException.class, () -> filmController.postFilm(film));

        assertEquals("Дата релиза не может быть раньше  28 декабря 1895 года!", exc.getMessage());
    }

    @Test
    public void postFilmWithoutReleaseDateTest() {

        Film film = Film.builder()
                .id(1)
                .name("name1")
                .description("description1")
                .duration(111)
                .build();
        filmController.postFilm(film);

        assertTrue(filmController.getAllFilms().contains(film), "Не добавлен фильм без даты релиза");
    }

    @Test
    public void updateFilmWithOneIdTest() {

        Film film1 = Film.builder()
                .id(1)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        Film film2 = Film.builder()
                .id(1)
                .name("name2")
                .description("description2")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();

        filmController.postFilm(film1);
        filmController.updateFilm(film2);

        assertFalse(filmController.getAllFilms().contains(film1), "Фильм не обновлен");
        assertTrue(filmController.getAllFilms().contains(film2), "Фильм не обновлен");
    }

    @Test
    public void updateAnUncreatedFilmTest() {
        Film film1 = Film.builder()
                .id(1)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class, () -> filmController.updateFilm(film1));
        assertEquals("Фильм с id 1 не найден", e.getMessage());
    }
}