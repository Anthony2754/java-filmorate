package ru.yandex.practicum.filmorate.storage.memory.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.RepeatException;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.UserStorage;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public User postUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new RepeatException("Такой пользователь уже существует!");
        }

        if (user.getId() == 0) {
            idGenerate();
            user.setId(id);
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUser(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(long id) {
        checkUser(id);
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() { return users.values(); }

    private void checkUser(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователь с id %s не найден", id));
        }
    }

    private void idGenerate() {
        id++;
    }
}

