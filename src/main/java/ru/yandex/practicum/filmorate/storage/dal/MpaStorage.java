package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.MpaModel;
import java.util.Collection;

public interface MpaStorage {
    Collection<MpaModel> getAllMpa();
    MpaModel getMpaById(int mpaId);
}
