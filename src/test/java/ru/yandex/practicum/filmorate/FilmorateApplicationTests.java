package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.RepeatException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

    UserController userController;
    FilmController filmController;

    @Autowired
    public FilmorateApplicationTests(UserController userController, FilmController filmController) {
        this.userController = userController;
        this.filmController = filmController;
    }

    @Test
    public void postUserWithoutIdTest() {
        User user = User.builder()
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        assertNotEquals(0, userController.postUser(user).getId(), "Не сгенерирован id");
    }

    @Test
    public void postUserWhitOneIdTest() {

        User user1 = User.builder()
                .email("email2@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        userController.postUser(user1);

        User user2 = User.builder()
                .id(user1.getId())
                .email("newEmail2@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        RepeatException e = Assertions.assertThrows(
                RepeatException.class, () -> userController.postUser(user2));
        assertEquals("Такой пользователь уже существует!", e.getMessage());
    }

    @Test
    public void postUserWithInvalidLoginTest() {

        User user = User.builder()
                .email("email3@yandex.ru")
                .login("login 1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        ValidationException exc = Assertions.assertThrows(
                ValidationException.class, () -> userController.postUser(user));

        assertEquals("Логин должен быть без пробелов!", exc.getMessage());
    }

    @Test
    public void postUserWithoutNameTest() {
        User user = User.builder()
                .email("email4@yandex.ru")
                .login("login1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        assertEquals("login1", userController.postUser(user).getName(), "логин и имя не совпадают");
    }

    @Test
    public void postUserWithEmptyNameTest() {
        User user = User.builder()
                .email("email5@yandex.ru")
                .login("login1")
                .name(" ")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        assertEquals("login1", userController.postUser(user).getName(), "логин и имя не совпадают");
    }

    @Test
    public void postUserWithoutBirthdayTest() {

        User user = User.builder()
                .email("email6@yandex.ru")
                .login("login1")
                .name("name1")
                .build();

        assertNull(userController.postUser(user).getBirthday(), "Не создан пользователь без даты рождения");
    }

    @Test
    public void updateUserWithOneIdTest() {
        User user1 = User.builder()
                .email("email7@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        userController.postUser(user1);

        User user2 = User.builder()
                .id(user1.getId())
                .email("email77@yandex.ru")
                .login("login1")
                .name("name2")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        userController.updateUser(user2);
        assertFalse(userController.getAllUsers().contains(user1), "Пользователь не обновлен");
        assertTrue(userController.getAllUsers().contains(user2), "Пользователь не обновлен");
    }

    @Test
    public void updateUnpublishedUserTest() {
        User user1 = User.builder()
                .id(1)
                .email("email8@yandex.ru")
                .login("login1")
                .name("name2")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        NotFoundException exc = Assertions.assertThrows(
                NotFoundException.class, () -> userController.updateUser(user1));
        assertEquals("Пользователь с id 1 не найден", exc.getMessage());
    }

    @Test
    public void postFilmWithoutIdTest() {
        Film film = Film.builder()
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();

        assertNotEquals(0, filmController.postFilm(film).getId(), "Не сгенерирован id");
    }

    @Test
    public void postFilmWithOneIdTest() {
        Film film1 = Film.builder()
                .name("name2")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();

        filmController.postFilm(film1);

        Film film2 = Film.builder()
                .id(film1.getId())
                .name("name3")
                .description("description2")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();

        RepeatException exc = Assertions.assertThrows(
                RepeatException.class, () -> filmController.postFilm(film2));
        assertEquals("Кино уже добавлено!", exc.getMessage());
    }

    @Test
    public void postFilmWithoutDescriptionTest() {

        Film film = Film.builder()
                .name("name4")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        filmController.postFilm(film);

        assertTrue(filmController.getAllFilms().contains(film), "Не добавлен фильм без описания");
    }

    @Test
    public void postFilmOlderReleaseDateTest() {
        Film film = Film.builder()
                .name("name5")
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
                .name("name6")
                .description("description1")
                .duration(111)
                .build();
        filmController.postFilm(film);

        assertTrue(filmController.getAllFilms().contains(film), "Не добавлен фильм без даты релиза");
    }

    @Test
    public void updateFilmWithOneIdTest() {

        Film film1 = Film.builder()
                .name("name7")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();

        filmController.postFilm(film1);

        Film film2 = Film.builder()
                .id(film1.getId())
                .name("name8")
                .description("description2")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();

        filmController.updateFilm(film2);

        assertFalse(filmController.getAllFilms().contains(film1), "Фильм не обновлен");
        assertTrue(filmController.getAllFilms().contains(film2), "Фильм не обновлен");
    }

    @Test
    public void updateAnUncreatedFilmTest() {
        Film film1 = Film.builder()
                .name("name9")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class, () -> filmController.updateFilm(film1));
        assertEquals("Фильм с id " + film1.getId() + " не найден", e.getMessage());
    }

}