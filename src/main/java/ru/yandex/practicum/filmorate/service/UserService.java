package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.log.LogsUtil.saveInLog;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    private final FriendsStorage friendsStorage;

    public User postUser(User user) {

        User userInStorage = userStorage.postUser(checkOnValidation(user));

        saveInLog(HttpMethod.POST, "/users", userInStorage.toString());
        return userInStorage;
    }

    public User updateUser(User user) {

        User userInStorage = userStorage.updateUser(checkOnValidation(user));

        saveInLog(HttpMethod.PUT, "/users", userInStorage.toString());
        return userInStorage;
    }

    public User getUserById(long id) {

        User userInStorage = userStorage.getUserById(id);

        saveInLog(HttpMethod.GET, "/users/" + id, userInStorage.toString());
        return userInStorage;
    }

    public Collection<User> getAllUsers() {

        Collection<User> usersInStorage = userStorage.getAllUsers();

        saveInLog(HttpMethod.GET, "/users", usersInStorage.toString());
        return usersInStorage;
    }

    public void addInFriend(long id, long friendId) {

        boolean inFriends;
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);
        inFriends = friendsStorage.addInFriend(id, friendId);

        saveInLog(HttpMethod.PUT, "/users/" + id + "/friends/" + friendId, ((Boolean) inFriends).toString());
    }

    public void deleteFromFriends(long userId, long friendId) {

        boolean deleteFriends;
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        deleteFriends = friendsStorage.deleteFromFriends(userId, friendId);

        if (!deleteFriends) {
            throw new NotFoundException(
                    String.format("Пользователь с id %s не добавлен в друзья к пользователю с id %s",
                            friendId, userId));
        }

        saveInLog(HttpMethod.DELETE, "/users/" + userId + "/friends/" + friendId, ((Boolean) deleteFriends).toString());
    }

    public List<User> getFriendsList(long id) {

        userStorage.getUserById(id);
        List<User> listFriends = friendsStorage.getFriendsList(id).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());

        saveInLog(HttpMethod.GET, "/users/" + id + "/friends", listFriends.toString());
        return listFriends;
    }

    public List<User> getListMutualFriends(long userId, long friendId) {

        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        List<User> listMutualFriends = friendsStorage.getListMutualFriends(userId, friendId).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());

        saveInLog(HttpMethod.GET, "/users/" + userId + "/friends/common/" + friendId, listMutualFriends.toString());
        return listMutualFriends;
    }

    private User checkOnValidation(User user) {

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин должен быть без пробелов!");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return user;
    }
}

