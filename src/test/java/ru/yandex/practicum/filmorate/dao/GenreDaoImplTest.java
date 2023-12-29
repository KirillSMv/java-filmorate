package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.filmDao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDaoImplTest {
    private GenreDaoImpl genreDaoImpl;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        genreDaoImpl = new GenreDaoImpl(jdbcTemplate);
    }

    @Test
    public void testGetGenres() {
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));
        genres.add(new Genre(3, "Мультфильм"));
        genres.add(new Genre(4, "Триллер"));
        genres.add(new Genre(5, "Документальный"));
        genres.add(new Genre(6, "Боевик"));

        Set<Genre> savedGenres = new HashSet<>(genreDaoImpl.getGenres());
        assertEquals(genres, savedGenres);
    }

    @Test
    public void testGetGenreById() {
        Genre savedGenre = genreDaoImpl.getGenreById(1);

        assertEquals(new Genre(1, "Комедия"), savedGenre);

    }
}
