package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.*;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class, ObjectNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(final RuntimeException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(
                Map.of("Ошибка:", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({FilmAlreadyExistException.class})
    public ResponseEntity<Map<String, String>> handleAlreadyExistsException(final RuntimeException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(
                Map.of("Ошибка:", e.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler({FilmValidationException.class, UserValidationException.class,
            FriendshipException.class, FilmLikesException.class})
    public ResponseEntity<Map<String, String>> handleRequestError(final RuntimeException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(
                Map.of("Ошибка:", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleError(final Throwable e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(
                Map.of("Произошла ошибка:", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

