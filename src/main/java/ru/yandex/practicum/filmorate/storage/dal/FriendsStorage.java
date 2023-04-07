package ru.yandex.practicum.filmorate.storage.dal;


import java.util.List;

public interface FriendsStorage {
    boolean addInFriend(long userId, long friendId);
    boolean deleteFromFriends(long userId, long friendId);
    List<Long> getFriendsList(long userId);
    List<Long> getListMutualFriends(long userId, long friendId);
}
