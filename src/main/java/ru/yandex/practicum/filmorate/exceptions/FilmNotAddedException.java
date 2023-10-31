package ru.yandex.practicum.filmorate.exceptions;

public class FilmNotAddedException extends RuntimeException {
    public FilmNotAddedException(String message) {
        super(message);
    }
}
