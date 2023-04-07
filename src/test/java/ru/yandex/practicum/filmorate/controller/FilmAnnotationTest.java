package ru.yandex.practicum.filmorate.controller;


import javax.validation.*;

import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaModel;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmAnnotationTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void close() {
        validatorFactory.close();
    }

    @Test
    public void postFilmTest() {

        Film film = Film.builder()
                .id(1)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .mpaModel(MpaModel.builder().id(5).name("PG").build())
                .build();

        Set<ConstraintViolation<Film>> setViolations = validator.validate(film);
        assertTrue(setViolations.isEmpty());
    }

    @Test
    public void postFilmWithEmptyNameTest() {

        Film film = Film.builder()
                .id(1)
                .name(" ")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .mpaModel(MpaModel.builder().id(5).name("PG").build())
                .build();

        Set<ConstraintViolation<Film>> setViolations = validator.validate(film);
        assertEquals(setViolations.size(), 1);

        ConstraintViolation<Film> constraintViolation = setViolations.iterator().next();
        assertEquals("Укажите название фильма!", constraintViolation.getMessage());
        assertEquals("name", constraintViolation.getPropertyPath().toString());
        assertEquals(" ", constraintViolation.getInvalidValue());
    }

    @Test
    public void postFilmWithoutNameTest() {

        Film film = Film.builder()
                .id(1)
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .mpaModel(MpaModel.builder().id(5).name("PG").build())
                .build();

        Set<ConstraintViolation<Film>> setViolations = validator.validate(film);
        assertEquals(setViolations.size(), 1);

        ConstraintViolation<Film> constraintViolation = setViolations.iterator().next();
        assertEquals("Укажите название фильма!", constraintViolation.getMessage());
        assertEquals("name", constraintViolation.getPropertyPath().toString());
        assertNull(constraintViolation.getInvalidValue());
    }

    @Test
    public void postFilmWithLengthDescription201Test() {

        Film film = Film.builder()
                .id(1)
                .name("name1")
                .description(RandomString.make(201))
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .mpaModel(MpaModel.builder().id(5).name("PG").build())
                .build();

        Set<ConstraintViolation<Film>> setViolations = validator.validate(film);
        assertEquals(setViolations.size(), 1);

        ConstraintViolation<Film> constraintViolation = setViolations.iterator().next();
        assertEquals("Максимальная длинна описания 200 символов!", constraintViolation.getMessage());
        assertEquals("description", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void postFilmWithNegativeDurationTest() {

        Film film = Film.builder()
                .id(1)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(-1)
                .mpaModel(MpaModel.builder().id(5).name("PG").build())
                .build();

        Set<ConstraintViolation<Film>> setViolations = validator.validate(film);
        assertEquals(setViolations.size(), 1);

        ConstraintViolation<Film> constraintViolation = setViolations.iterator().next();
        assertEquals("Продолжительность должна быть положительной!", constraintViolation.getMessage());
        assertEquals("duration", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void postFilmWithZeroDurationTest() {

        Film film = Film.builder()
                .id(1)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(0)
                .mpaModel(MpaModel.builder().id(5).name("PG").build())
                .build();

        Set<ConstraintViolation<Film>> setViolations = validator.validate(film);
        assertEquals(setViolations.size(), 1);

        ConstraintViolation<Film> constraintViolation = setViolations.iterator().next();
        assertEquals("Продолжительность должна быть положительной!", constraintViolation.getMessage());
        assertEquals("duration", constraintViolation.getPropertyPath().toString());
    }

    @Test
    void addFilmWithoutMpaTest() {
        Film film = Film.builder()
                .id(1)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.of(2020, 11, 12))
                .duration(111)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(violations.size(), 1);

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Mpa must not be null", violation.getMessage());
        assertEquals("MPA", violation.getPropertyPath().toString());
    }
}

