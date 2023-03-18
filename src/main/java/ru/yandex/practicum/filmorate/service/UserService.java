package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private long id = 0;

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

    public Collection<User> getUsers() {

        Collection<User> usersInStorage = userStorage.getAllUsers();

        saveInLog(HttpMethod.GET, "/users", usersInStorage.toString());
        return usersInStorage;
    }

    public List<User> addInFriend(long id, long friendId) {

        userStorage.getUserById(id).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(id);
        List<User> friends = List.of(userStorage.getUserById(id), userStorage.getUserById(friendId));

        saveInLog(HttpMethod.PUT, "/users/" + id + "/friends/", friends.toString());
        return friends;
    }

    public List<User> deleteFromFriends(long userId, long friendId) {

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (!user.getFriends().contains(friendId)) {
            throw new NotFoundException(
                    String.format("Пользователь с id %s не добавлен в друзья к пользователю с id %s",
                            friendId, userId));
        }

        if (!friend.getFriends().contains(userId)) {
            throw new NotFoundException(
                    String.format("Пользователь с id %s не добавлен в друзья к пользователю с id %s",
                    userId, friendId));
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        List<User> friends = List.of(user, friend);

        saveInLog(HttpMethod.DELETE, "/users/" + userId + "/friends/" + friendId, friends.toString());
        return friends;
    }

    public List<User> getFriendsList(long id) {

        Set<Long> friends = userStorage.getUserById(id).getFriends();
        List<User> friendsList = userStorage.getAllUsers().stream()
                .filter(user -> friends.contains(user.getId()))
                .collect(Collectors.toList());

        saveInLog(HttpMethod.GET, "/users/" + id + "/friends", friendsList.toString());
        return friendsList;
    }

    public List<User> getListMutualFriends(long userId, long friendId) {

        User user = userStorage.getUserById(userId);
        User friendUser = userStorage.getUserById(friendId);

        Set<Long> friends = user.getFriends();
        Set<Long> otherFriends = friendUser.getFriends();

        List<User> mutualFriends = userStorage.getAllUsers().stream()
                .filter(u -> friends.contains(u.getId()))
                .filter(u -> otherFriends.contains(u.getId()))
                .collect(Collectors.toList());

        saveInLog(HttpMethod.GET, "/users/" + userId + "/friends/common/" + friendId, mutualFriends.toString());
        return mutualFriends;
    }

    private void idGenerate() {
        id++;
    }

    private User checkOnValidation(User user) {

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин должен быть без пробелов!");
        }

        if (user.getId() == 0) {
            idGenerate();
            user.setId(id);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return user;
    }

    private void saveInLog(HttpMethod method, String uri, String storage) {
        log.info("Получен запрос: '{} {}'. В хранилище: '{}'", method, uri, storage);
    }

}

