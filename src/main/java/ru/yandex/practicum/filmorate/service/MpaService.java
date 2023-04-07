package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<MPA> getMpa() {
        Collection<MPA> MPAInStorage = mpaStorage.getAllMpa();
        saveInLog(HttpMethod.GET, "/mpa", MPAInStorage.toString());
        return MPAInStorage;
    }

    public MPA getMpaById(int id) {
        MPA MPAInStorage = mpaStorage.getMpaById(id);
        saveInLog(HttpMethod.GET, "/mpa/" + id, MPAInStorage.toString());
        return MPAInStorage;
    }

    private void saveInLog(HttpMethod method, String uri, String storage) {
        log.info("Получен запрос: '{} {}'. В хранилище: '{}'", method, uri, storage);
    }
}
