package ru.yandex.practicum.filmorate.exceptions;

import java.time.LocalDate;

public class FilmValidationException extends RuntimeException {
    String name;
    String description;
    LocalDate releaseDate;
    int duration;

    public FilmValidationException(String message) {
        super(message);
    }

    public FilmValidationException(String message, String name, String description, LocalDate releaseDate, int duration) {
        this(message);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void getFullMessage() {
        System.out.printf(getMessage() + " Заданные параметры: %s, %s, %s, %d", name, description, releaseDate, duration);
    }
}
