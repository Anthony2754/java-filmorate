package ru.yandex.practicum.filmorate.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

@Slf4j
public class Logs {

    public static void logRequests(HttpMethod method, String uri, String body) {

        log.info("Получен запрос: '{} {}'. Тело запроса: '{}'", method, uri, body);
    }

    public static void saveInLog(HttpMethod method, String uri, String storage) {
        log.info("Получен запрос: '{} {}'. В хранилище: '{}'", method, uri, storage);
    }
}
