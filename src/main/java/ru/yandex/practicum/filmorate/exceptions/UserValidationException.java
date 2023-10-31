package ru.yandex.practicum.filmorate.exceptions;

import java.time.LocalDate;

public class UserValidationException extends RuntimeException {
    String email;
    String login;
    LocalDate birthday;

    public UserValidationException(String message) {
        super(message);
    }

    public UserValidationException(String message, String email, String login, LocalDate birthday) {
        this(message);
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

    public void getFullMessage() {
        System.out.printf(getMessage() + " Заданные параметры: %s, %s, %s", email, login, birthday);
    }
}


