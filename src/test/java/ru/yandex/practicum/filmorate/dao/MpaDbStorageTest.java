package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaStorage;

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
    void getAllMpaTest() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        assertThat(mpaStorage.getAllMpa(), hasSize(5));
        assertThat(mpaStorage.getAllMpa(), hasItem(mpa));
    }

    @Test
    void getMpaByIdTest() {
        Mpa mpaId1 = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Mpa mpaId5 = Mpa.builder()
                .id(5)
                .name("NC-17")
                .build();

        assertThat(mpaStorage.getMpaById(1), equalTo(mpaId1));
        assertThat(mpaStorage.getMpaById(5), equalTo(mpaId5));
    }
}
