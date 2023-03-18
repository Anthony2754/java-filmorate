package ru.yandex.practicum.filmorate.controller;


import javax.validation.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAnnotationTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    public void postUserTest() {
        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        Set<ConstraintViolation<User>> setViolations = validator.validate(user);
        assertTrue(setViolations.isEmpty());
    }

    @Test
    public void postUserWithEmptyEmailTest() {
        User user = User.builder()
                .id(1)
                .email(" ")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        Set<ConstraintViolation<User>> setViolations = validator.validate(user);
        assertEquals(setViolations.size(), 1);

        ConstraintViolation<User> constraintViolation = setViolations.iterator().next();
        assertEquals("Введите существующий адрес электронной почты!", constraintViolation.getMessage());
        assertEquals("email", constraintViolation.getPropertyPath().toString());
        assertEquals(" ", constraintViolation.getInvalidValue());
    }

    @Test
    public void postUserWithInvalidEmailTest() {
        User user = User.builder()
                .id(1)
                .email("@yandex.ru")
                .login("user1")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        Set<ConstraintViolation<User>> setViolations = validator.validate(user);
        assertEquals(setViolations.size(), 1);

        ConstraintViolation<User> constraintViolation = setViolations.iterator().next();
        assertEquals("Введите существующий адрес электронной почты!", constraintViolation.getMessage());
        assertEquals("email", constraintViolation.getPropertyPath().toString());
        assertEquals("@yandex.ru", constraintViolation.getInvalidValue());
    }

    @Test
    public void postUserWithEmptyLoginTest() {
        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login(" ")
                .name("name1")
                .birthday(LocalDate.of(2001, 8, 15))
                .build();
        Set<ConstraintViolation<User>> setViolations = validator.validate(user);
        assertEquals(setViolations.size(), 1);

        ConstraintViolation<User> constraintViolation = setViolations.iterator().next();
        assertEquals("Укажите логин!", constraintViolation.getMessage());
        assertEquals("login", constraintViolation.getPropertyPath().toString());
        assertEquals(" ", constraintViolation.getInvalidValue());
    }

    @Test
    public void postUserWithBirthdayInFutureTest() {
        User user = User.builder()
                .id(1)
                .email("email1@yandex.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(2222, 2, 2))
                .build();
        Set<ConstraintViolation<User>> setViolations = validator.validate(user);
        assertEquals(setViolations.size(), 1);

        ConstraintViolation<User> constraintViolation = setViolations.iterator().next();
        assertEquals("Дата рождения не может быть в будущем!", constraintViolation.getMessage());
        assertEquals("birthday", constraintViolation.getPropertyPath().toString());
        assertEquals(LocalDate.of(2222, 2, 2), constraintViolation.getInvalidValue());
    }
}

