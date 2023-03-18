package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserStorage {

    User postUser(User user);

    User updateUser(User user);

    User getUserById(long id);
    Collection<User> getAllUsers();
}

