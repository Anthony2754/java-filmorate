package ru.yandex.practicum.filmorate.storage.dao;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> getAllGenres() {
        String request = "SELECT * FROM genres";
        return jdbcTemplate.query(request, this::rowInGenre);
    }

    @Override
    public Genre getGenreById(int genreId) {
        Genre genre;
        String request = "SELECT * FROM genres WHERE genre_id = ?";
        try {
            genre = jdbcTemplate.queryForObject(request, this::rowInGenre, genreId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("Жанр с id %s не найден", genreId));
        }
        return genre;
    }

    private Genre rowInGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
