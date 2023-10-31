package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotAddedException;
import ru.yandex.practicum.filmorate.exceptions.FilmParametersException;
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
    Map<Integer, Film> films = new HashMap<>();
    private int idOfFilm;

    @PostMapping("/films")
    public Film postFilm(@RequestBody Film film) throws FilmValidationException, FilmAlreadyExistsException, FilmParametersException {
        boolean areParametersInCorrect = checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());

        if (areParametersInCorrect) {
            log.debug("Некорректно указаны параметры фильма {}, {}, {}, {}", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
            throw new FilmValidationException("Неверное указаны параметры.", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        }
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
    public Film updateFilm(@RequestBody Film film) throws FilmValidationException, FilmParametersException, FilmNotAddedException {
        boolean areParametersIncorrect = checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());

        if (areParametersIncorrect) {
            log.debug("Некорректно указаны параметры фильма {}, {}, {}, {}", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
            throw new FilmValidationException("Неверное указаны параметры.", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        }
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

    public boolean checkParameters(String name, String description, LocalDate releaseDate, Integer duration) throws FilmParametersException {
        if (checkIfNotSet(name, description, releaseDate, duration)) {
            log.debug("Заданы не все параметры фильма {}, {}, {}, {}", name, description, releaseDate, duration);
            throw new FilmParametersException("Пожалуйста, убедитесь, что заданы все параметры для фильма: name, description, releaseDate, duration");
        }
        return name.isBlank() || description.length() > 200 || releaseDate.isBefore(LocalDate.of(1895, DECEMBER, 28))
                || releaseDate.isAfter(LocalDate.now()) || duration <= 0;
    }

    private boolean checkIfNotSet(String name, String description, LocalDate releaseDate, Integer duration) {
        return name == null || description == null || releaseDate == null || duration == null;
    }

    private int generateId() {
        return ++idOfFilm;
    }
}
