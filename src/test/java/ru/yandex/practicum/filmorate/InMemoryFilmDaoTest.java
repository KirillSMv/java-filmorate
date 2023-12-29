package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmDao;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryFilmDaoTest {
    private InMemoryFilmDao inMemoryFilmDao;

    @BeforeEach
    public void setup() {
        inMemoryFilmDao = new InMemoryFilmDao();
    }

    @Test
    public void testAddFilm() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(100)
                .build();
        assertEquals(film, inMemoryFilmDao.addFilm(film));
    }

    @Test
    public void testAddFilmWhenDuplication() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2020, 10, 20))
                .build();
        inMemoryFilmDao.getFilmsMap().put(1, film);
        assertThrows(FilmAlreadyExistException.class, () -> inMemoryFilmDao.addFilm(film));
    }
}

