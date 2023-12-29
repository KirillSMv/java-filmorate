package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.filmDao.FilmDao;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Repository("inMemoryFilmDao")
public class InMemoryFilmDao implements FilmDao {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idOfFilm;

    @Override
    public Film addFilm(Film film) {
        checkIfFilmAdded(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkIfFilmExists(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        checkIfFilmExists(id);
        return films.get(id);
    }

    @Override
    public void deleteFilmById(Integer id) {
        checkIfFilmExists(id);
        films.remove(id);
    }

    @Override
    public void addLike(Integer id, Integer userId) {

    }

    @Override
    public void deleteLike(Integer id, Integer userId) {

    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return null;
    }

    private int generateId() {
        return ++idOfFilm;
    }

    public void checkIfFilmExists(Integer id) {
        if (!films.containsKey(id)) {
            log.error("фильм с id {} еще не добавлен.", id);
            throw new FilmNotFoundException(String.format("фильм с id %d еще не добавлен.", id));
        }
    }

    private void checkIfFilmAdded(Film film) {
        Optional<Film> savedFilm = films.values().stream()
                .filter(element -> element.equals(film))
                .findFirst();
        if (savedFilm.isPresent()) {
            log.error("такой фильм уже добавлен");
            throw new FilmAlreadyExistException("такой фильм уже добавлен");
        }
    }

    public Map<Integer, Film> getFilmsMap() {
        return films;
    }

}

