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
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreStorage;

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
    void getAllGenresTest() {
        Genre genre = Genre.builder()
                .id(6)
                .name("Боевик")
                .build();

        assertThat(genreStorage.getAllGenres(), hasSize(6));
        assertThat(genreStorage.getAllGenres(), hasItem(genre));
    }

    @Test
    void getGenreByIdTest() {
        Genre genre1 = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();
        Genre genre6 = Genre.builder()
                .id(3)
                .name("Мультфильм")
                .build();
        assertThat(genreStorage.getGenreById(1), equalTo(genre1));
        assertThat(genreStorage.getGenreById(3), equalTo(genre6));
    }
}
