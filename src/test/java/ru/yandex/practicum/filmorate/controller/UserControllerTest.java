package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.RepeatException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

    @Test
    public void postUserWithoutIdTest() {
        User user = User.builder()
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        assertEquals(1, userController.postUser(user).getId(), "Не сгенерирован id");
    }

    @Test
    public void postUserWhitOneIdTest() {

        User user1 = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        User user2 = User.builder()
                .id(1)
                .email("email2@yandex.ru")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        userController.postUser(user1);

        RepeatException exc = Assertions.assertThrows(
                RepeatException.class, () -> userController.postUser(user2));
        assertEquals("Такой пользователь уже существует!", exc.getMessage());
    }

    @Test
    public void postUserWithInvalidLoginTest() {

        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
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
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        assertEquals("login1", userController.postUser(user).getName(), "логин и имя не совпадают");
    }

    @Test
    public void postUserWithEmptyNameTest() {
        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .name(" ")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        assertEquals("login1", userController.postUser(user).getName(), "логин и имя не совпадают");
    }

    @Test
    public void postUserWithoutBirthdayTest() {

        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .build();

        assertNull(userController.postUser(user).getBirthday(), "Не создан пользователь без даты рождения");
    }

    @Test
    public void updateUserWithOneIdTest() {
        User user1 = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        userController.postUser(user1);

        User user2 = User.builder()
                .id(1)
                .email("email1@yandex.ru")
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
                .email("email1@yandex.ru")
                .login("login1")
                .name("name2")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        NotFoundException exc = Assertions.assertThrows(
                NotFoundException.class, () -> userController.updateUser(user1));
        assertEquals("Пользователь с id 1 не найден", exc.getMessage());
    }
}