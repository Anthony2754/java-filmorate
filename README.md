# java-filmorate
Схема БД

![Схема БД](/images/Filmorate_DB.png)


* Связь между пользователями "Многие ко многим" (friends).
  Поле status отражает статус дружбы: TRUE если подтверждена и FALSE если не подтверждена


* Связь между пользователями и фильмами "Многие ко многим" (favorite_films). 


* Связь между фильмами и жанрами "Многие ко многим" (film_genre).


* Связь между рейтингом и фильмами "Один к многим" (rating_MPA).


Примеры запросов:

* @PostMapping("/users")
* @PutMapping("/users")
* @PutMapping("/users/{userId}/friends/{friendId}")
* @DeleteMapping("/users/{userId}/friends/{friendId}")
* @GetMapping("/users")
* @GetMapping("/users/{id}")
* @GetMapping("/users/{id}/friends")
* @GetMapping("/users/{userId}/friends/common/{friendId}")
 

* @PostMapping("/films")
* @PutMapping("/films")
* @PutMapping("/films/{filmId}/like/{userId}")
* @DeleteMapping("/films/{filmId}/like/{userId}")
* @GetMapping("/films")
* @GetMapping("/films/{id}")
* @GetMapping("/films/popular?count={count}")


