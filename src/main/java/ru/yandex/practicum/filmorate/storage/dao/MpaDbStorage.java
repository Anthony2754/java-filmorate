package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> getAllMpa() {
        String request = "SELECT * FROM rating_mpa";
        return jdbcTemplate.query(request, this::rowInMpa);
    }

    @Override
    public Mpa getMpaById(int mpaId) {
        Mpa MPA;
        String request = "SELECT * FROM rating_mpa WHERE mpa_id = ?";
        try {
            MPA = jdbcTemplate.queryForObject(request, this::rowInMpa, mpaId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("MPA с id %s не найдено", mpaId));
        }
        return MPA;
    }

    private Mpa rowInMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
