package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmGenreDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDbStorage filmGenreStorage;
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
    void addGenreTest() {
        Genre genre1 = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();

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

        Film addFilm1 = filmStorage.postFilm(film1);
        filmGenreStorage.addGenre(List.of(genre1), addFilm1.getId());
        assertThat(filmStorage.getFilmById(addFilm1.getId()).getGenres(), hasItem(genre1));
    }

    @Test
    void getGenresListTest() {
        Genre genreId3 = Genre.builder()
                .id(3)
                .name("Мультфильм")
                .build();

        Film film = Film.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .rate(3)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(genreId3))
                .build();

        Film addFilm = filmStorage.postFilm(film);
        assertThat(filmGenreStorage.getGenresList(addFilm.getId()), hasItem(genreId3.getId()));
    }

    @Test
    void deleteGenreTest() {
        Genre genreId2 = Genre.builder()
                .id(3)
                .name("Мультфильм")
                .build();

        Film film = Film.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .rate(3)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(genreId2))
                .build();

        Film addFilm = filmStorage.postFilm(film);
        filmGenreStorage.deleteGenre(addFilm.getId());

        assertThat(filmGenreStorage.getGenresList(addFilm.getId()), empty());
    }
}
