package ru.yandex.practicum.filmorate.dao.filmDao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilmById(Integer id);

    Film getFilmById(Integer id);

    List<Film> getFilms();

    void addLike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

    List<Film> getPopularFilms(Integer count);
}
