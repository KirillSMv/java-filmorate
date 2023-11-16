package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmLikesException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage inMemoryFilmStorage;

    public FilmService(FilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Film addFilm(Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    public Film getFilmById(Integer id) {
        return inMemoryFilmStorage.getFilmById(id);
    }

    public void deleteFilmById(Integer id) {
        inMemoryFilmStorage.deleteFilmById(id);
    }

    public List<Film> getFilms() {
        return inMemoryFilmStorage.getFilms();
    }

    public void addLike(Integer id, Integer userId) {
        Film film = inMemoryFilmStorage.getFilmById(id);
        boolean isLikeAdded = film.addLike(userId);
        if (!isLikeAdded) {
            log.error("Пользователь с id {} уже ставил лайк фильму с id {}", userId, id);
            throw new FilmLikesException(String.format("Пользователь с id %d уже ставил лайк фильму с id %d", userId, id));
        }
    }

    public void deleteLike(Integer id, Integer userId) {
        Film film = inMemoryFilmStorage.getFilmById(id);
        boolean isLikeDeleted = film.getLikes().remove(userId);
        if (!isLikeDeleted) {
            log.error("Пользователь с id {} не ставил лайк фильму с id {}", userId, id);
            throw new FilmLikesException(String.format("Пользователь с id %d не ставил лайк фильму с id %d", userId, id));
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        return inMemoryFilmStorage.getFilms().stream()
                .sorted(this::compareFilms)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compareFilms(Film film1, Film film2) {
        return Integer.reverse(Integer.compare(film1.getLikes().size(), film2.getLikes().size()));
    }
}
