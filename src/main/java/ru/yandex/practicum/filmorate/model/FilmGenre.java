package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmGenre {
    private int film_id;
    private int genre_id;
    private String genre;

    public FilmGenre(int film_id, int genre_id, String genre) {
        this.film_id = film_id;
        this.genre_id = genre_id;
        this.genre = genre;
    }
}
