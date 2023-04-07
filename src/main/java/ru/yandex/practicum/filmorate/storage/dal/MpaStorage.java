package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.MPA;
import java.util.Collection;

public interface MpaStorage {
    Collection<MPA> getAllMpa();
    MPA getMpaById(int mpaId);
}
