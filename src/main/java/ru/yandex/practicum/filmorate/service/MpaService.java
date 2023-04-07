package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaModel;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<MpaModel> getMpa() {
        Collection<MpaModel> mpaModelInStorage = mpaStorage.getAllMpa();
        saveInLog(HttpMethod.GET, "/mpa", mpaModelInStorage.toString());
        return mpaModelInStorage;
    }

    public MpaModel getMpaById(int id) {
        MpaModel mpaModelInStorage = mpaStorage.getMpaById(id);
        saveInLog(HttpMethod.GET, "/mpa/" + id, mpaModelInStorage.toString());
        return mpaModelInStorage;
    }

    private void saveInLog(HttpMethod method, String uri, String storage) {
        log.info("Получен запрос: '{} {}'. В хранилище: '{}'", method, uri, storage);
    }
}
