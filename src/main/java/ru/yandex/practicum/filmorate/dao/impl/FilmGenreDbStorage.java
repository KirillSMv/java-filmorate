package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Repository
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*
    Наш сервис фильмов будет вызывать методы этого класса для реализации функциональности: добавление
    -- Для получения жанра и рейтинга по идентификатору:

    Нам пришел фильм со списком id жанров, мы добавили фильм в таблицу, нужно добавить теперь жанры
    для этого фильма в таблицу FILM_GENRE
    связь будет что 1 фильм и несколько жанров, то есть
    1 - 3
    1 - 2
    2 - 4
    2 - 2
    нужно добавить значения в таблицу, используем данные из фильм и добавить через batch update

     */

    @Override
    public Collection<Genre> getGenersByFilmId(Integer filmId) {
        return null;
    }

    @Override
    public void addFilmGenres(Film film) {

    }
}
