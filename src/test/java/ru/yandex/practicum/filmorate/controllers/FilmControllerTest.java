package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController filmController = new FilmController();

    @Test
    public void createFilmWithoutIdTest() {

        Film film = Film.builder()
                .name("name_1")
                .description("description_1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();

        assertEquals(1, filmController.postFilm(film).getId(), "Не сгенерирован id");
    }

    @Test
    public void createFilmWithNegativeIdTest() {

        Film film = Film.builder()
                .id(-1)
                .name("name_1")
                .description("description_1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();

        ValidationException exc = Assertions.assertThrows(
                ValidationException.class, () -> filmController.postFilm(film));
        assertEquals("id должно быть положительным!", exc.getMessage());
    }

    @Test
    public void createFilmWithOneIdTest() {

        Film film1 = Film.builder()
                .id(1)
                .name("name_1")
                .description("description_1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        Film film2 = Film.builder()
                .id(1)
                .name("name_2")
                .description("description_2")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        filmController.postFilm(film1);

        ValidationException exc = Assertions.assertThrows(
                ValidationException.class, () -> filmController.postFilm(film2));
        assertEquals("Кино уже добавлено!", exc.getMessage());
    }

    @Test
    public void createFilmWithoutDescriptionTest() {

        Film film = Film.builder()
                .id(1)
                .name("name_1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        filmController.postFilm(film);

        assertTrue(filmController.getFilms().contains(film), "Не добавлен фильм без описания");
    }

    @Test
    public void createFilmOlderReleaseDateTest() {

        Film film = Film.builder()
                .id(1)
                .name("name_1")
                .description("description_1")
                .releaseDate(LocalDate.of(1020, 11, 12))
                .duration(111)
                .build();

        ValidationException e = Assertions.assertThrows(
                ValidationException.class, () -> filmController.postFilm(film));

        assertEquals("Дата релиза не может быть раньше  28 декабря 1895 года!", e.getMessage());
    }

    @Test
    public void createFilmWithoutReleaseDateTest() {

        Film film = Film.builder()
                .id(1)
                .name("name_1")
                .description("description_1")
                .duration(111)
                .build();
        filmController.postFilm(film);

        assertTrue(filmController.getFilms().contains(film), "Не добавлен фильм без даты релиза");
    }

    @Test
    public void updateFilmWithoutIdTest() {

        Film film = Film.builder()
                .name("name_1")
                .description("description_1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();

        assertEquals(1, filmController.putFilm(film).getId(), "Не сгенерирован id");
    }

    @Test
    public void updateFilmWithOneIdTest() {

        Film film1 = Film.builder()
                .id(1)
                .name("name_1")
                .description("description_1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        Film film2 = Film.builder()
                .id(1)
                .name("name_2")
                .description("description_2")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();

        filmController.putFilm(film1);
        filmController.putFilm(film2);

        assertFalse(filmController.getFilms().contains(film1), "Пользователь не обновлен");
        assertTrue(filmController.getFilms().contains(film2), "Пользователь не обновлен");
    }

    @Test
    public void updateFilmWithoutDescriptionTest() {

        Film film = Film.builder()
                .id(1)
                .name("name_1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        filmController.putFilm(film);

        assertTrue(filmController.getFilms().contains(film), "Не добавлен фильм без описания");
    }

    @Test
    public void updateFilmOlderReleaseDateTest() {

        Film film = Film.builder()
                .id(1)
                .name("name_1")
                .description("description_1")
                .releaseDate(LocalDate.of(1020, 11, 12))
                .duration(111)
                .build();

        ValidationException e = Assertions.assertThrows(
                ValidationException.class, () -> filmController.putFilm(film));

        assertEquals("Дата релиза не может быть раньше  28 декабря 1895 года!", e.getMessage());
    }

    @Test
    public void updateFilmWithoutReleaseDateTest() {

        Film film = Film.builder()
                .id(1)
                .name("name_1")
                .description("description_1")
                .duration(111)
                .build();
        filmController.putFilm(film);

        assertTrue(filmController.getFilms().contains(film), "Не добавлен фильм без даты релиза");
    }
}