package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.Collection;

public interface MpaStorage {

    Collection<Mpa> getAllMpa();

    Mpa getMpaById(int mpaId);
}
