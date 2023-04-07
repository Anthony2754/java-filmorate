package ru.yandex.practicum.filmorate.storage.dao;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.LikesStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final LikesStorage likesStorage;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final FilmGenreStorage filmGenreStorage;

    @Override
    public Collection<Film> getAllFilms() {
        String request = "SELECT * FROM films";
        return jdbcTemplate.query(request, this::rowInFilm);
    }

    @Override
    public Film postFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long filmId = simpleJdbcInsert.executeAndReturnKey(inMap(film)).longValue();

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenreStorage.addGenre(film.getGenres(), filmId);
        }
        return getFilmById(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        String request = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rate = ?, " +
                "mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(request
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getRate()
                , film.getMpaModel().getId()
                , film.getId());

        Film oldFilm = getFilmById(film.getId());

        Collection<Genre> genre = oldFilm.getGenres();
        if (genre != null && !genre.isEmpty()) {
            filmGenreStorage.deleteGenre(film.getId());
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenreStorage.addGenre(film.getGenres(), film.getId());
        }
        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(long filmId) {
        Film film;
        String request = "SELECT * FROM films WHERE film_id = ?";
        try {
            film = jdbcTemplate.queryForObject(request, this::rowInFilm, filmId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("Фильм с id %s не найден", filmId));
        }
        return film;
    }

    private Film rowInFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .mpaModel(mpaService.getMpaById(resultSet.getInt("mpa_id")))
                .likes(likesStorage.getLikesList(resultSet.getLong("film_id")))
                .genres(genreService.getListOfGenres(resultSet.getLong("film_id")))
                .build();
    }

    private Map<String, Object> inMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rate", film.getRate());
        values.put("MPA_id", film.getMpaModel().getId());
        return values;
    }
}
