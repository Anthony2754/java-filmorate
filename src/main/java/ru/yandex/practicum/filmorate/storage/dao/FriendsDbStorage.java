package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.storage.dal.FriendsStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendsDbStorage implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addInFriend(long userId, long friendId) {
        Friends friends = Friends.builder()
                .userId(userId)
                .friendId(friendId)
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friends");
        return simpleJdbcInsert.execute(saveInMap(friends)) > 0;
    }

    @Override
    public boolean deleteFromFriends(long userId, long friendId) {
        String request = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(request, userId, friendId) > 0;
    }

    @Override
    public List<Long> getFriendsList(long userId) {
        String request = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.queryForList(request, Long.class, userId);
    }

    @Override
    public List<Long> getListMutualFriends(long userId, long friendId) {
        String request = "SELECT friend_id FROM (SELECT *  FROM friends WHERE user_id = ? OR user_id = ?) " +
                "GROUP BY friend_id HAVING (COUNT(*) > 1)";
        return jdbcTemplate.queryForList(request, Long.class, userId, friendId);
    }

    private Map<String, Object> saveInMap(Friends friends) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_Id", friends.getUserId());
        values.put("friend_Id", friends.getFriendId());
        return values;
    }
}
