package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmGenre {
    private int filmId;
    private int genreId;
    private String genre;

    public FilmGenre(int filmId, int genreId, String genre) {
        this.filmId = filmId;
        this.genreId = genreId;
        this.genre = genre;
    }
}
