package ru.yandex.practicum.filmorate.exeptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handlerValidationException(ValidationException exc) {
        log.warn("ValidationException", exc);
        return new ErrorResponse(400, "Bad Request",exc.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handlerRepeatException(RepeatException exc) {
        log.warn("RepeatException", exc);
        return new ErrorResponse(400, "Bad Request",exc.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handlerNotFoundException(NotFoundException exc) {
        log.warn("NotFoundException", exc);
        return new ErrorResponse(404, "Not Found", exc.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerThrowable(final Throwable exc) {
        log.warn("Throwable", exc);
        return new ErrorResponse(500, "Internal Server Error", "Произошла не предвиденная ошибка.");
    }
}

