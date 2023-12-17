package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.filmDao.FilmDao;
import ru.yandex.practicum.filmorate.dao.userDao.UserDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private FilmDao filmDao;
    private UserDao userDao;

    public FilmService(@Qualifier("FilmDaoImpl") FilmDao filmDao, @Qualifier("UserDaoImpl") UserDao userDao) {
        this.filmDao = filmDao;
        this.userDao = userDao;
    }

    public Film addFilm(Film film) {
        return filmDao.addFilm(film);
    }

    public Film getFilmById(Integer id) {
        return filmDao.getFilmById(id);
    }

    public List<Film> getFilms() {
        return filmDao.getFilms();
    }

    public void deleteFilmById(Integer id) {
        filmDao.deleteFilmById(id);
    }

    public Film updateFilm(Film film) {
        return filmDao.updateFilm(film);
    }

    public void addLike(Integer id, Integer userId) {
        filmDao.addLike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        filmDao.deleteLike(id, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmDao.getPopularFilms(count);
    }
}
