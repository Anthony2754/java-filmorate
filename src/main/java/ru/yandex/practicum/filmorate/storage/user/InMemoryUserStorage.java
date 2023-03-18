package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.RepeatException;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public User postUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new RepeatException("Такой пользователь уже существует!");
        }
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        checkUser(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    public User getUserById(long id) {
        checkUser(id);
        return users.get(id);
    }

    public Collection<User> getAllUsers() { return users.values(); }

    private void checkUser(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователь с id %s не найден", id));
        }
    }
}

