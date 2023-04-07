CREATE TABLE IF NOT EXISTS users
(
    user_id  Integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    varchar,
    login    varchar NOT NULL,
    name     varchar,
    birthday date CHECK (birthday <= CURRENT_DATE),
    CONSTRAINT login_is_not_empty CHECK (login <> ''),
    CONSTRAINT no_spaces_in_login CHECK (login NOT IN (' '))
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id Integer PRIMARY KEY,
    name     varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id   Integer REFERENCES users (user_id),
    friend_id Integer REFERENCES users (user_id),
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS rating_MPA
(
    MPA_id Integer PRIMARY KEY,
    name   varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      Integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar NOT NULL,
    description  varchar(200),
    release_date date,
    duration     Integer,
    rate         Integer,
    MPA_id       Integer REFERENCES rating_MPA (MPA_id),
    CONSTRAINT name_is_not_empty CHECK (name <> ''),
    CONSTRAINT duration_positive CHECK (duration > 0)
);

CREATE TABLE IF NOT EXISTS likes
(
    user_id Integer REFERENCES users (user_id),
    film_id Integer REFERENCES films (film_id),
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  Integer REFERENCES films (film_id),
    genre_id Integer REFERENCES genres (genre_id),
    PRIMARY KEY (film_id, genre_id)
);