package ru.yandex.practicum.filmorate.controllers;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @PostMapping
    public User postUser (@Valid @RequestBody User user) {
        logRequests("POST", "/users", user.toString());
        User user1 = checkOnValidation(user);
        if (!users.containsKey(user1.getId())) {
            users.put(user1.getId(), user1);
        } else {
            throw new ValidationException("Такой пользователь уже существует!");
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        logRequests("PUT", "/users", user.toString());
        User user1 = checkOnValidation(user);
        users.put(user1.getId(), user1);
        return user;
    }

    @GetMapping
    public Collection<User> getUsers() {
        logRequests("GET", "/users", "no body");
        return users.values();
    }

    private User checkOnValidation(User user) {
        if (user.getId() == 0) {
            idGenerate();
            user.setId(id);
        }
        if (user.getId() < 0) {
            throw new ValidationException("id должно быть положительным!");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин должен быть без пробелов!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    private void idGenerate() {
        id++;
    }

    private void logRequests(String method, String uri, String body) {
        log.info("Получен запрос: '{} {}'. Тело запроса: '{}'", method, uri, body);
    }
}
