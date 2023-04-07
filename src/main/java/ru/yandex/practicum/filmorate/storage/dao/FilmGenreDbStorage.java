package ru.yandex.practicum.filmorate.storage.dao;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addGenre(List<Genre> genres, long filmId) {
        String request = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        List<Genre> uniqueGenres = genres.stream()
                .distinct()
                .collect(Collectors.toList());
        getJdbcTemplate().batchUpdate(request, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = uniqueGenres.get(i);
                ps.setLong(1, filmId);
                ps.setInt(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return uniqueGenres.size();
            }
        });
    }

    @Override
    public void deleteGenre(long filmId) {
        String request = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(request, filmId);
    }

    @Override
    public List<Integer> getGenresList(long id) {
        String request = "SELECT genre_id FROM film_genre WHERE film_id = ?";
        return jdbcTemplate.queryForList(request, Integer.class, id);
    }

    private JdbcOperations getJdbcTemplate() {
        return jdbcTemplate;
    }
}
