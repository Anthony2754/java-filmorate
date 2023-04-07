package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        logRequests(HttpMethod.GET, "/mpa", "no body");
        return mpaService.getMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        logRequests(HttpMethod.GET, "/mpa/" + id, "no body");
        return mpaService.getMpaById(id);
    }

    private void logRequests(HttpMethod method, String uri, String body) {
        log.info("Получен запрос: '{} {}'. Тело запроса: '{}'", method, uri, body);
    }
}
