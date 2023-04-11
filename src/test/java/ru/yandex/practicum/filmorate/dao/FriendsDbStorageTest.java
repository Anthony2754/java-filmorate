package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.time.LocalDate;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.contains;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendsDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userStorage;
    private final FriendsDbStorage friendsStorage;

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
    void getEmptyFriendsTest() {
        Collection<Long> friends = friendsStorage.getFriendsList(1);
        assertThat("Список друзей не пуст", friends, hasSize(0));
    }

    @Test
    void addInFriendsTest() {
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
        friendsStorage.addInFriend(addUser1.getId(), addUser2.getId());

        assertThat("User2 не добавлен в друзья User1",
                userStorage.getUserById(addUser1.getId()).getFriends(), hasItem(addUser2.getId()));
        assertThat("Список друзей User2 не пуст",
                userStorage.getUserById(addUser2.getId()).getFriends(), empty());
    }

    @Test
    void deleteFromFriendsTest() {
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
        friendsStorage.addInFriend(addUser1.getId(), addUser2.getId());

        assertThat("Список друзей User1 пуст",
                userStorage.getUserById(addUser1.getId()).getFriends(), hasItem(addUser2.getId()));

        friendsStorage.deleteFromFriends(addUser1.getId(), addUser2.getId());
        assertThat("Список друзей User1 не пуст",
                userStorage.getUserById(addUser1.getId()).getFriends(), empty());
    }

    @Test
    void getFriendsListTest() {
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

        User user3 = User.builder()
                .email("email3@yandex.ru")
                .login("login3")
                .name("User3")
                .birthday(LocalDate.of(2002, 8, 15))
                .build();

        User addUser1 = userStorage.postUser(user1);
        User addUser2 = userStorage.postUser(user2);
        User addUser3 = userStorage.postUser(user3);

        friendsStorage.addInFriend(addUser1.getId(), addUser2.getId());
        friendsStorage.addInFriend(addUser1.getId(), addUser3.getId());
        assertThat("Список друзей User1 не содержит id User2 и User3",
                friendsStorage.getFriendsList(addUser1.getId()), contains(addUser2.getId(), addUser3.getId()));
    }

    @Test
    void getListMutualFriendsTest() {
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

        User user3 = User.builder()
                .email("email3@yandex.ru")
                .login("login3")
                .name("User3")
                .birthday(LocalDate.of(2002, 8, 15))
                .build();

        User addUser1 = userStorage.postUser(user1);
        User addUser2 = userStorage.postUser(user2);
        User addUser3 = userStorage.postUser(user3);

        friendsStorage.addInFriend(addUser1.getId(), addUser3.getId());
        friendsStorage.addInFriend(addUser2.getId(), addUser3.getId());
        assertThat("Список друзей User не содержит id User2 и User3",
                friendsStorage.getListMutualFriends(addUser1.getId(), addUser2.getId()), contains(addUser3.getId()));
    }
}
