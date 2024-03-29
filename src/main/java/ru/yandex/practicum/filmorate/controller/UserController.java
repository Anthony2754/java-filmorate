package ru.yandex.practicum.filmorate.controller;


import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.log.LogsUtil;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {

        LogsUtil.logRequests(HttpMethod.POST, "/users", user.toString());
        return userService.postUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {

        LogsUtil.logRequests(HttpMethod.PUT, "/users", user.toString());
        return userService.updateUser(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addInFriend(@PathVariable long userId, @PathVariable long friendId) {

        LogsUtil.logRequests(HttpMethod.PUT, "/users/" + userId + "/friends/" + friendId, "no body");
        userService.addInFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable long userId, @PathVariable long friendId) {

        LogsUtil.logRequests(HttpMethod.DELETE, "/users/" + userId + "/friends/" + friendId, "no body");
        userService.deleteFromFriends(userId, friendId);
    }

    @GetMapping
    public Collection<User> getAllUsers() {

        LogsUtil.logRequests(HttpMethod.GET, "/users", "no body");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {

        LogsUtil.logRequests(HttpMethod.GET, "/users/" + id, "no body");
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getListFriends(@PathVariable long id) {

        LogsUtil.logRequests(HttpMethod.GET, "/users/" + id + "/friends", "no body");
        return userService.getFriendsList(id);
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public List<User> getListMutualFriends(@PathVariable long userId, @PathVariable long friendId) {

        LogsUtil.logRequests(HttpMethod.GET, "/users/" + userId + "/friends/common/" + friendId, "no body");
        return userService.getListMutualFriends(userId, friendId);
    }
}
