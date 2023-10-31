package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.FilmParametersException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerValidationTest {
    FilmController controller = new FilmController();

    @Test
    public void givenBlankName_whenCheckParameters_thenValidationFailed() {
        Film film = Film.builder()
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(2020, 10, 10))
                .duration(100)
                .build();
        boolean areParametersInCorrect = controller.checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        assertEquals(true, areParametersInCorrect);
    }

    @Test
    public void givenLongDescription_whenCheckParameters_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. " +
                        "о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.")
                .releaseDate(LocalDate.of(2020, 10, 10))
                .duration(100)
                .build();
        boolean areParametersInCorrect = controller.checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        assertEquals(true, areParametersInCorrect);
    }

    @Test
    public void givenIncorrectReleaseDate_whenCheckParameters_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .build();
        boolean areParametersInCorrect = controller.checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        assertEquals(true, areParametersInCorrect);
    }

    @Test
    public void givenReleaseDateInTheFuture_whenCheckParameters_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now().plusDays(1))
                .duration(100)
                .build();
        boolean areParametersInCorrect = controller.checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        assertEquals(true, areParametersInCorrect);
    }

    @Test
    public void givenZeroDuration_whenCheckParameters_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2020, 10, 20))
                .duration(0)
                .build();
        boolean areParametersInCorrect = controller.checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        assertEquals(true, areParametersInCorrect);
    }

    @Test
    public void givenIncorrectDuration_whenCheckParameters_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2020, 10, 20))
                .duration(-1)
                .build();
        boolean areParametersInCorrect = controller.checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        assertEquals(true, areParametersInCorrect);
    }

    @Test
    public void givenEmptyName_whenCheckParameters_thenValidationFailed() {
        Film film = Film.builder()
                .description("description")
                .releaseDate(LocalDate.of(2020, 10, 20))
                .duration(100)
                .build();

        FilmParametersException exception = assertThrows(
                FilmParametersException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        boolean areParametersInCorrect = controller.checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
                    }
                });
        assertEquals("Пожалуйста, убедитесь, что заданы все параметры для фильма: name, description, releaseDate, duration", exception.getMessage());
    }

    @Test
    public void givenEmptyDescription_whenCheckParameters_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .releaseDate(LocalDate.of(2020, 10, 20))
                .duration(100)
                .build();

        FilmParametersException exception = assertThrows(
                FilmParametersException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        boolean areParametersInCorrect = controller.checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
                    }
                });
        assertEquals("Пожалуйста, убедитесь, что заданы все параметры для фильма: name, description, releaseDate, duration", exception.getMessage());
    }

    @Test
    public void givenEmptyReleaseDate_whenCheckParameters_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .duration(100)
                .build();

        FilmParametersException exception = assertThrows(
                FilmParametersException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        boolean areParametersInCorrect = controller.checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
                    }
                });
        assertEquals("Пожалуйста, убедитесь, что заданы все параметры для фильма: name, description, releaseDate, duration", exception.getMessage());
    }

    @Test
    public void givenEmptyDuration_whenCheckParameters_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2020, 10, 20))
                .build();

        FilmParametersException exception = assertThrows(
                FilmParametersException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        boolean areParametersInCorrect = controller.checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
                    }
                });
        assertEquals("Пожалуйста, убедитесь, что заданы все параметры для фильма: name, description, releaseDate, duration", exception.getMessage());
    }

    @Test
    public void givenCorrectParameters_whenCheckParameters_thenValidationFailed() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(100)
                .build();

        boolean areParametersInCorrect = controller.checkParameters(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        assertEquals(false, areParametersInCorrect);
    }
}

