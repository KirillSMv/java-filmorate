package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotAddedException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.time.Month.DECEMBER;

@Slf4j
@RestController
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idOfFilm;

    @PostMapping("/films")
    public Film postFilm(@RequestBody Film film) throws FilmValidationException, FilmAlreadyExistsException {
        checkParameters(film);
        film.setId(generateId());
        if (films.containsKey(film.getId())) {
            log.debug("фильм с id {} уже существует.", film.getId());
            throw new FilmAlreadyExistsException("фильм с id " + film.getId() + " уже добавлен");
        }
        films.put(film.getId(), film);
        log.debug("Сохраняемый объект: {}", film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) throws FilmValidationException, FilmNotAddedException {
        checkParameters(film);
        if (!films.containsKey(film.getId())) {
            throw new FilmNotAddedException("Такого фильма еще нет, пожалуйста, в начале добавьте фильм с id: " + film.getId());
        }
        films.put(film.getId(), film);
        log.debug("Сохраняемый объект: {}", film);
        return film;
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        log.debug("Количество фильмов на текущий момент: {}", films.size());
        return films.values();
    }

    private static boolean checkParameters(Film film) throws FilmValidationException {
        if (checkIfNotSet(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration())) {
            log.debug("Заданы не все параметры фильма {}, {}, {}, {}", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
            throw new FilmValidationException("Пожалуйста, убедитесь, что заданы все параметры для фильма: name, description, releaseDate, duration");
        }
        if (film.getName().isBlank()) {
            String msg = "имя не может быть пустым";
            log.error(msg);
            throw new FilmValidationException(msg);
        }
        if (film.getDescription().length() > 200) {
            String msg = "количество символов описания фильма не может быть больше 200";
            log.error(msg);
            throw new FilmValidationException(msg);
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, DECEMBER, 28))) {
            log.error("дата релиза фильма не может быть ранее 28 декабря 1895 года, введенная дата: {}", film.getReleaseDate());
            throw new FilmValidationException("дата релиза фильма не может быть ранее 28 декабря 1895 года");
        }
        if (film.getReleaseDate().isAfter(LocalDate.now())) {
            log.error("дата релиза фильма не может быть в будущем, введенная дата: {}", film.getReleaseDate());
            throw new FilmValidationException("дата релиза фильма не может быть в будущем");
        }
        if (film.getDuration() <= 0) {
            log.error("длительность фильма не может быть менее или равна 0, введенная длительность {}", film.getDuration());
            throw new FilmValidationException("длительность фильма не может быть менее или равна 0");
        }
        return true;
    }

    private static boolean checkIfNotSet(String name, String description, LocalDate releaseDate, Integer duration) {
        return name == null || description == null || releaseDate == null || duration == null;
    }

    private int generateId() {
        return ++idOfFilm;
    }
}
