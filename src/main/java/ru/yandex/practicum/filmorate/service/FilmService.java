package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmLikesStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikesStorage filmLikesDbStorage;
    private final GenreDao genreDao;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage, FilmLikesStorage filmLikesDbStorage, GenreDao genreDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikesDbStorage = filmLikesDbStorage;
        this.genreDao = genreDao;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public List<Genre> getGenres() {
        return genreDao.getGenres();
    }

    public Genre getGenreById(Integer id) {
        return genreDao.getGenreById(id);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public void deleteFilmById(Integer id) {
        filmStorage.deleteFilmById(id);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLike(Integer id, Integer userId) {
        filmLikesDbStorage.addLike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        filmLikesDbStorage.deleteLike(id, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = filmLikesDbStorage.getPopularFilms();
        films.addAll(filmLikesDbStorage.getNotPopularFilms());
        return films.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}
