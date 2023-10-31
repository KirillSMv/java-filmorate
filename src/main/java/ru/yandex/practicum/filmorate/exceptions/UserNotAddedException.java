package ru.yandex.practicum.filmorate.exceptions;

public class UserNotAddedException extends RuntimeException {
    public UserNotAddedException(String message) {
        super(message);
    }
}
