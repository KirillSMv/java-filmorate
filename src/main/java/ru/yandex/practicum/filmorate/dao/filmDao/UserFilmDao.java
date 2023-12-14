package ru.yandex.practicum.filmorate.dao.filmDao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface UserFilmDao {
    void addLike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

    List<Film> getPopularFilms();

    List<Film> getNotPopularFilms();
}
