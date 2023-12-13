package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface FilmGenreStorage {
    Collection<Genre> getGenersByFilmId(Integer id);

    void addFilmGenres(Film film);
}
