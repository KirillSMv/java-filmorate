package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmLike {
    private int userId;
    private int filmId;

    public FilmLike(int userId, int filmId) {
        this.userId = userId;
        this.filmId = filmId;
    }
}
