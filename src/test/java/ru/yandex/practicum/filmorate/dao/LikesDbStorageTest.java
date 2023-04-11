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
import ru.yandex.practicum.filmorate.model.User;
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
public class LikesDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final LikesDbStorage likesStorage;

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
    void getEmptyLikesTest() {

        Collection<Long> likes = likesStorage.getLikesList(1);
        assertThat("Список лайков не пуст", likes, hasSize(0));
    }

    @Test
    void addLikeTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();

        User addUser1 = userStorage.postUser(user1);
        Film addFilm1 = filmStorage.postFilm(film1);

        likesStorage.addLike(addFilm1.getId(), addUser1.getId());
        assertThat(String.format("%s не поставил лайк %s", addUser1.getName(), addFilm1.getName()),
                filmStorage.getFilmById(addFilm1.getId()).getLikes(), hasItem(addUser1.getId()));
    }

    @Test
    void deleteLikeTest() {
        User user1 = User.builder()
                .email("email@yandex.ru")
                .login("login")
                .name("User")
                .birthday(LocalDate.of(2001, 8, 15))
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

        User addUser1 = userStorage.postUser(user1);
        Film addFilm1 = filmStorage.postFilm(film1);

        likesStorage.addLike(addFilm1.getId(), addUser1.getId());
        assertThat(String.format("Список лайков %s пуст", addFilm1.getName()),
                filmStorage.getFilmById(addFilm1.getId()).getLikes(), hasItem(addUser1.getId()));
        likesStorage.deleteLike(addFilm1.getId(), addUser1.getId());
        assertThat(String.format("Список лайков %s не пуст", addFilm1.getName()),
                filmStorage.getFilmById(addFilm1.getId()).getLikes(), empty());
    }

    @Test
    void getLikesListTest() {
        User user1 = User.builder()
                .email("email@yandex.ru")
                .login("login")
                .name("User")
                .birthday(LocalDate.of(2001, 8, 15))
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

        User addUser1 = userStorage.postUser(user1);
        Film addFilm1 = filmStorage.postFilm(film1);

        likesStorage.addLike(addFilm1.getId(), addUser1.getId());
        assertThat(String.format("Список лайков %s не содержит id %s = %s",
                        addFilm1.getName(), addUser1.getName(), addUser1.getId()),
                likesStorage.getLikesList(addFilm1.getId()), contains(addUser1.getId()));
    }

    @Test
    void getBestFilmsTest() {
        User user1 = User.builder()
                .email("email@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        User user2 = User.builder()
                .email("email2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        Film film1 = Film.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .rate(1)
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

        User addUser1 = userStorage.postUser(user1);
        User addUser2 = userStorage.postUser(user2);
        Film addFilm1 = filmStorage.postFilm(film1);
        Film addFilm2 = filmStorage.postFilm(film2);

        likesStorage.addLike(addFilm1.getId(), addUser1.getId());
        likesStorage.addLike(addFilm1.getId(), addUser2.getId());

        assertThat("Список лучших фильмов отличается от [1, 2]",
                likesStorage.getBestFilms(5), contains(addFilm1.getId(), addFilm2.getId()));
        assertThat("Список лучших фильмов отличается от [1]",
                likesStorage.getBestFilms(1), hasItem(addFilm1.getId()));
    }
}
