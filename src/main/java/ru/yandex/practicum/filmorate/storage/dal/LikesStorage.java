package ru.yandex.practicum.filmorate.storage.dal;

import java.util.List;

public interface LikesStorage {
    boolean addLike(long filmId, long userId);
    boolean deleteLike(long filmId, long userId);
    List<Long> getLikesList(long filmId);
    List<Long> getBestFilms(int count);

}
