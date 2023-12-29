package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static java.time.Month.DECEMBER;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        checkParameters(film);
        return filmService.addFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") Integer id) {
        return filmService.getFilmById(id);
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @DeleteMapping("/{id}")
    public String deleteFilm(@PathVariable("id") Integer id) {
        filmService.deleteFilmById(id);
        return String.format("фильм с id %d удален", id);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        checkParameters(film);
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public String addLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        filmService.addLike(id, userId);
        return String.format("Пользователь с id %d поставил лайк фильму с id %d", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public String deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        filmService.deleteLike(id, userId);
        return String.format("Пользователь с id %d убрал лайк фильму с id %d", userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getPopularFilms(count);
    }

    private void checkParameters(Film film) {
        if (checkIfNotSet(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration())) {
            log.error("Заданы не все параметры фильма {}, {}, {}, {}", film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration());
            throw new FilmValidationException("Пожалуйста, убедитесь, что заданы все параметры для фильма: " +
                    "name, description, releaseDate, duration");
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
    }

    private boolean checkIfNotSet(String name, String description, LocalDate releaseDate, Integer duration) {
        return name == null || description == null || releaseDate == null || duration == null;
    }
}
