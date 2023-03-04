package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController = new UserController();

    @Test
    public void createUserWithoutIdTest() {

        User user = User.builder()
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        assertEquals(1, userController.postUser(user).getId(), "Не сгенерирован id");
    }

    @Test
    public void createUserWithNegativeId() {

        User user = User.builder()
                .id(-1)
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        ValidationException exc = Assertions.assertThrows(
                ValidationException.class, () -> userController.postUser(user));
        assertEquals("id должно быть положительным!", exc.getMessage());
    }

    @Test
    public void createUserWhitOneIdTest() {

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

        ValidationException exc = Assertions.assertThrows(
                ValidationException.class, () -> userController.postUser(user2));
        assertEquals("Такой пользователь уже существует!", exc.getMessage());
    }

    @Test
    public void createUserWithInvalidLoginTest() {

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
    public void createUserWithoutNameTest() {

        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        assertEquals("login1", userController.postUser(user).getName(), "login и name не совпадают");
    }

    @Test
    public void createUserWithEmptyNameTest() {

        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .name(" ")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        assertEquals("login1", userController.postUser(user).getName(), "login и name не совпадают");
    }

    @Test
    public void createUserWithoutBirthdayTest() {

        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .build();

        assertNull(userController.postUser(user).getBirthday(), "Не создан пользователь без даты рождения");
    }

    @Test
    public void updateUserTest() {

        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        userController.updateUser(user);

        assertTrue(userController.getUsers().contains(user), "Пользователь не создан");
    }

    @Test
    public void updateUserWithoutIdTest() {

        User user = User.builder()
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        assertEquals(1, userController.updateUser(user).getId(), "Не сгенерирован id");
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
        User user2 = User.builder()
                .id(1)
                .email("email2@yandex.ru")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        userController.updateUser(user1);
        userController.updateUser(user2);

        assertFalse(userController.getUsers().contains(user1), "Пользователь не обновлен");

        assertTrue(userController.getUsers().contains(user2), "Пользователь не обновлен");
    }

    @Test
    public void updateUserWithInvalidLoginTest() {

        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login 1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        ValidationException exc = Assertions.assertThrows(
                ValidationException.class, () -> userController.updateUser(user));

        assertEquals("Логин должен быть без пробелов!", exc.getMessage());
    }

    @Test
    public void updateUserWithoutNameTest() {

        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        assertEquals("login1", userController.updateUser(user).getName(), "login и name не совпадают");
    }

    @Test
    public void updateUserWithEmptyNameTest() {

        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .name(" ")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        assertEquals("login1", userController.updateUser(user).getName(), "login и name не совпадают");
    }

    @Test
    public void updateUserWithoutBirthdayTest() {

        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .build();

        assertNull(userController.updateUser(user).getBirthday(), "Не создан пользователь без даты рождения");
        //я не совсем понял что значит: не хватает переноса строки;
        // весь код в IDEA точно помещается до специальной линии с права и отступы тоже расположены как положено
    }
}