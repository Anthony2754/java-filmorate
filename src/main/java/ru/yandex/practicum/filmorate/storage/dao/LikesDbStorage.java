package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Likes;
import ru.yandex.practicum.filmorate.storage.dal.LikesStorage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addLike(long filmId, long userId) {
        Likes likes = Likes.builder()
                .filmId(filmId)
                .userId(userId)
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("likes");
        return simpleJdbcInsert.execute(saveInMap(likes)) > 0;
    }

    @Override
    public boolean deleteLike(long filmId, long userId) {
        String request = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.update(request, filmId, userId) > 0;
    }

    @Override
    public List<Long> getLikesList(long filmId) {
        String request = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(request, Long.class, filmId);
    }

    @Override
    public List<Long> getBestFilms(int count) {
        String request = "SELECT films.film_id FROM films " +
                "LEFT OUTER JOIN likes ON films.film_id = likes.film_id " +
                "GROUP BY films.film_id " +
                "ORDER BY COUNT(DISTINCT likes.user_id) desc " +
                "limit + ?";
        List<Long> likes = jdbcTemplate.queryForList(request, Long.class, count);
        return likes;
    }

    private Map<String, Object> saveInMap(Likes likes) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_Id", likes.getUserId());
        values.put("film_Id", likes.getFilmId());
        return values;
    }
}
