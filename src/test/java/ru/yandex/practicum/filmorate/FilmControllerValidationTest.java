/*
package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.dao.filmDao.FilmStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerValidationTest {
    private FilmController controller;
    private FilmStorage inMemoryFilmStorage;
    private FilmService filmService;


    @BeforeEach
    public void setup() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        //filmService = new FilmService(inMemoryFilmStorage);
        controller = new FilmController(filmService);
    }

    @Test
    public void givenBlankName_whenPostFilm_thenValidationFailed() {
        Film film = Film.builder()
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(2020, 10, 10))
                .duration(100)
                .build();
        assertThrows(FilmValidationException.class, () -> controller.postFilm(film));
    }

    @Test
    public void givenLongDescription_whenPostFilm_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. " +
                        "о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.")
                .releaseDate(LocalDate.of(2020, 10, 10))
                .duration(100)
                .build();
        assertThrows(FilmValidationException.class, () -> controller.postFilm(film));
    }

    @Test
    public void givenIncorrectReleaseDate_whenPostFilm_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .build();
        assertThrows(FilmValidationException.class, () -> controller.postFilm(film));
    }

    @Test
    public void givenReleaseDateInTheFuture_whenPostFilm_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now().plusDays(1))
                .duration(100)
                .build();
        assertThrows(FilmValidationException.class, () -> controller.postFilm(film));
    }

    @Test
    public void givenZeroDuration_whenPostFilm_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2020, 10, 20))
                .duration(0)
                .build();
        assertThrows(FilmValidationException.class, () -> controller.postFilm(film));
    }

    @Test
    public void givenIncorrectDuration_whenPostFilm_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2020, 10, 20))
                .duration(-1)
                .build();
        assertThrows(FilmValidationException.class, () -> controller.postFilm(film));
    }

    @Test
    public void givenEmptyName_whenPostFilm_thenValidationFailed() {
        Film film = Film.builder()
                .description("description")
                .releaseDate(LocalDate.of(2020, 10, 20))
                .duration(100)
                .build();
        assertThrows(FilmValidationException.class, () -> controller.postFilm(film));
    }

    @Test
    public void givenEmptyDescription_whenPostFilm_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .releaseDate(LocalDate.of(2020, 10, 20))
                .duration(100)
                .build();
        assertThrows(FilmValidationException.class, () -> controller.postFilm(film));
    }

    @Test
    public void givenEmptyReleaseDate_whenPostFilm_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .duration(100)
                .build();
        assertThrows(FilmValidationException.class, () -> controller.postFilm(film));
    }

    @Test
    public void givenEmptyDuration_whenPostFilm_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2020, 10, 20))
                .build();
        assertThrows(FilmValidationException.class, () -> controller.postFilm(film));
    }

    @Test
    public void givenCorrectParameters_whenCheckParameters_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(100)
                .build();
        assertEquals(film, controller.postFilm(film));
    }
}

*/
