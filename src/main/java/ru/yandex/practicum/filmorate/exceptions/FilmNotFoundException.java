package ru.yandex.practicum.filmorate.exceptions;

public class FilmExistingException extends RuntimeException {

    public FilmExistingException(String message) {
        super(message);
    }
}
