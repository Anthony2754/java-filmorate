package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmStorage;

    @AfterEach
    void afterEach() {
        jdbcTemplate.update("DELETE FROM LIKES");
        jdbcTemplate.update("DELETE FROM FILM_GENRE");
        jdbcTemplate.update("DELETE FROM FRIENDS");
        jdbcTemplate.update("DELETE FROM USERS");
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1");
    }

    @Test
    void postFilmTest() {
        Film film = Film.builder()
                .name("Film")
                .description("description")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .rate(3)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();

        Film addFilm = filmStorage.postFilm(film);
        film.setId(1);
        assertThat(film, equalTo(addFilm));
    }

    @Test
    void updateFilmTest() {
        Film film1 = Film.builder()
                .name("Film1")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .rate(3)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(1).name("Комедия").build()))
                .build();
        Film oldFilm = filmStorage.postFilm(film1);

        Film film2 = Film.builder()
                .id(oldFilm.getId())
                .name("Film2")
                .description("description2")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .rate(5)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film updateFilm = filmStorage.updateFilm(film2);
        assertThat("Фильм не обновлен", film2, equalTo(updateFilm));
    }

    @Test
    void updateFilmWithNonexistentIdTest() {
        Film film = Film.builder()
                .id(111)
                .name("Film")
                .description("description")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .rate(3)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(1).name("Комедия").build()))
                .build();

        NotFoundException e = Assertions.assertThrows(NotFoundException.class, () -> filmStorage.updateFilm(film));
        assertThat("Фильм с id 111 не найден", equalTo(e.getMessage()));
    }

    @Test
    void getEmptyFilmsTest() {
        Collection<Film> films = filmStorage.getAllFilms();
        assertThat("Список фильмов не пуст", films, empty());
    }

    @Test
    void getFilmInvalidIdTest() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class, () -> filmStorage.getFilmById(1));
        assertThat("Фильм с id 1 не найден", equalTo(e.getMessage()));
    }

    @Test
    void getAllFilmsTest() {
        Film film1 = Film.builder()
                .name("Film1").description("Description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .rate(3)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film film2 = Film.builder()
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(2022, 11, 12))
                .duration(111)
                .rate(5)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();

        Film addFilm1 = filmStorage.postFilm(film1);
        Film addFilm2 = filmStorage.postFilm(film2);

        assertThat("Список пользователей пуст", filmStorage.getAllFilms(), hasSize(2));
        assertThat("Film1 не найден", filmStorage.getAllFilms(), hasItem(addFilm1));
        assertThat("Film2 не найден", filmStorage.getAllFilms(), hasItem(addFilm2));
    }

    @Test
    void getFilmByIdTest() {
        Film film1 = Film.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .rate(3)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();

        Film addFilm = filmStorage.postFilm(film1);
        assertThat(addFilm, equalTo(filmStorage.getFilmById(addFilm.getId())));
    }
}
