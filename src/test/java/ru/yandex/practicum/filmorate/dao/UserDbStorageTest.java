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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userStorage;

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
    void postUserTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login("login")
                .name("User")
                .birthday(LocalDate.of(2001, 8, 15))
                .friends(new ArrayList<>())
                .build();

        User newUser = userStorage.postUser(user);
        user.setId(1);
        assertThat(user, equalTo(newUser));
    }

    @Test
    void updateUserTest() {
        User user1 = User.builder()
                .email("email@yandex.ru")
                .login("login")
                .name("User")
                .birthday(LocalDate.of(2001, 8, 15))
                .friends(new ArrayList<>())
                .build();

        User oldUser = userStorage.postUser(user1);

        User user2 = User.builder()
                .id(oldUser.getId())
                .email("newEmail@yandex.ru")
                .login("newLogin")
                .name("NewUser")
                .birthday(LocalDate.of(2001, 8, 15))
                .friends(new ArrayList<>())
                .build();

        User updateUser = userStorage.updateUser(user2);
        assertThat("Пользователь не обновлен", user2, equalTo(updateUser));
    }

    @Test
    void updateUserWithNonexistentIdTest() {
        User user = User.builder()
                .id(111)
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(2001, 8, 15))
                .friends(new ArrayList<>())
                .build();

        NotFoundException e = Assertions.assertThrows(NotFoundException.class, () -> userStorage.updateUser(user));
        assertThat("Пользователь с id 111 не найден", equalTo(e.getMessage()));
    }

    @Test
    void getEmptyUsersTest() {
        Collection<User> users = userStorage.getAllUsers();
        assertThat("Список пользователей не пуст", users, empty());
    }

    @Test
    void getUserEmptyIdTest() {

        NotFoundException e = Assertions.assertThrows(NotFoundException.class, () -> userStorage.getUserById(1));
        assertThat("Пользователь с id 1 не найден", equalTo(e.getMessage()));
    }

    @Test
    void getUserByIdTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login("login")
                .name("User")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        User addUser = userStorage.postUser(user);
        assertThat(addUser, equalTo(userStorage.getUserById(addUser.getId())));
    }

    @Test
    void getAllUsersTest() {
        User user1 = User.builder()
                .email("email@yandex.ru")
                .login("login")
                .name("User")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        User user2 = User.builder()
                .email("email2@yandex.ru")
                .login("login2")
                .name("User2")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();

        User addUser1 = userStorage.postUser(user1);
        User addUser2 = userStorage.postUser(user2);

        assertThat("Список пользователей пуст", userStorage.getAllUsers(), hasSize(2));
        assertThat("User1 не найден", userStorage.getAllUsers(), hasItem(addUser1));
        assertThat("User2 не найден", userStorage.getAllUsers(), hasItem(addUser2));
    }
}
