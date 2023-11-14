package ru.yandex.practicum.filmorate.exceptions;

public class UserExistingException extends RuntimeException {

    public UserExistingException(String message) {
        super(message);
    }

}

