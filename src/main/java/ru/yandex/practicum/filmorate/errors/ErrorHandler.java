package ru.yandex.practicum.filmorate.errors;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.RepeatException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handlerValidationException(ValidationException e) {
        log.warn("ValidationException", e);
        return new ErrorResponse(400, "Bad Request",e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handlerRepeatException(RepeatException e) {
        log.warn("RepeatException", e);
        return new ErrorResponse(400, "Bad Request",e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handlerNotFoundException(NotFoundException e) {
        log.warn("NotFoundException", e);
        return new ErrorResponse(404, "Not Found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerThrowable(final Throwable e) {
        log.warn("Throwable", e);
        return new ErrorResponse(500, "Internal Server Error", "Произошла не предвиденная ошибка.");
    }
}

