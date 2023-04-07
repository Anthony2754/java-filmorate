package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<Mpa> getMpa() {
        Collection<Mpa> MpaInStorage = mpaStorage.getAllMpa();
        saveInLog(HttpMethod.GET, "/mpa", MpaInStorage.toString());
        return MpaInStorage;
    }

    public Mpa getMpaById(int id) {
        Mpa MpaInStorage = mpaStorage.getMpaById(id);
        saveInLog(HttpMethod.GET, "/mpa/" + id, MpaInStorage.toString());
        return MpaInStorage;
    }

    private void saveInLog(HttpMethod method, String uri, String storage) {
        log.info("Получен запрос: '{} {}'. В хранилище: '{}'", method, uri, storage);
    }
}
