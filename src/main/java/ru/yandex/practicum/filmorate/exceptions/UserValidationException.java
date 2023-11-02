package ru.yandex.practicum.filmorate.exceptions;

import java.time.LocalDate;

public class UserValidationException extends RuntimeException {

    public UserValidationException(String message) {
        super(message);
    }
}


