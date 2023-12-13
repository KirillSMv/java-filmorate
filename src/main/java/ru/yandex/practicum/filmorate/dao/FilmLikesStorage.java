package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLikes;

import java.util.List;

public interface FilmLikesStorage {
    void addLike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

    List<Film> getPopularFilms();

    List<Film> getNotPopularFilms();

    FilmLikes getRating(Film film); //todo
}
