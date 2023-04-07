package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaModel;
import ru.yandex.practicum.filmorate.storage.dal.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final FilmStorage filmStorage;
    private final FriendsStorage friendsStorage;
    private final GenreStorage genreStorage;
    private final LikesStorage likesStorage;
    private final MpaStorage mpaStorage;


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
    void getUserById() {
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
    void getUsersTest() {
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

    @Test
    void postFilmTest() {
        Film film = Film.builder()
            .name("Film")
            .description("description")
            .releaseDate(LocalDate.of(2020, 11, 12))
            .duration(111)
            .rate(3)
            .MPAModel(MpaModel.builder().id(1).name("G").build())
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
            .MPAModel(MpaModel.builder().id(1).name("G").build())
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
            .MPAModel(MpaModel.builder().id(1).name("G").build())
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
            .MPAModel(MpaModel.builder().id(1).name("G").build())
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
    void getFilmsTest() {
        Film film1 = Film.builder()
                .name("Film1").description("Description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .rate(3)
                .MPAModel(MpaModel.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film film2 = Film.builder()
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(2022, 11, 12))
                .duration(111)
                .rate(5)
                .MPAModel(MpaModel.builder().id(1).name("G").build())
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
    void getFilmById() {
        Film film1 = Film.builder()
            .name("Film")
            .description("Description")
            .releaseDate(LocalDate.of(2020, 11, 12))
            .duration(111)
            .rate(3)
            .MPAModel(MpaModel.builder().id(1).name("G").build())
            .likes(new ArrayList<>())
            .genres(List.of(Genre.builder().id(2).name("Драма").build()))
            .build();

        Film addFilm = filmStorage.postFilm(film1);
        assertThat(addFilm, equalTo(filmStorage.getFilmById(addFilm.getId())));
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
            .MPAModel(MpaModel.builder().id(1).name("G").build())
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
            .MPAModel(MpaModel.builder().id(1).name("G").build())
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
    void getListLikes() {
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
            .MPAModel(MpaModel.builder().id(1).name("G").build())
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
            .MPAModel(MpaModel.builder().id(1).name("G").build())
            .likes(new ArrayList<>())
            .genres(List.of(Genre.builder().id(2).name("Драма").build()))
            .build();

        Film film2 = Film.builder()
            .name("Film2")
            .description("Description2")
            .releaseDate(LocalDate.of(2022, 11, 12))
            .duration(111)
            .rate(5)
            .MPAModel(MpaModel.builder().id(1).name("G").build())
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

    @Test
    void getGenresTest() {
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
            .MPAModel(MpaModel.builder().id(1).name("G").build())
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
            .MPAModel(MpaModel.builder().id(1).name("G").build())
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
            .MPAModel(MpaModel.builder().id(1).name("G").build())
            .likes(new ArrayList<>())
            .genres(List.of(genreId2))
            .build();

        Film addFilm = filmStorage.postFilm(film);
        filmGenreStorage.deleteGenre(addFilm.getId());

        assertThat(filmGenreStorage.getGenresList(addFilm.getId()), empty());
    }

    @Test
    void getMpaTest() {
        MpaModel mpaModel = MpaModel.builder()
                .id(1)
                .name("G")
                .build();

        assertThat(mpaStorage.getAllMpa(), hasSize(5));
        assertThat(mpaStorage.getAllMpa(), hasItem(mpaModel));
    }

    @Test
    void getMpaById() {
        MpaModel mpaModelId1 = MpaModel.builder()
            .id(1)
            .name("G")
            .build();

        MpaModel mpaModelId5 = MpaModel.builder()
            .id(5)
            .name("NC-17")
            .build();

        assertThat(mpaStorage.getMpaById(1), equalTo(mpaModelId1));
        assertThat(mpaStorage.getMpaById(5), equalTo(mpaModelId5));
    }
}