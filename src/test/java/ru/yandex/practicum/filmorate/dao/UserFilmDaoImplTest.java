package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dao.filmDao.impl.UserFilmDaoImpl;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserFilmDaoImplTest {
    private UserFilmDaoImpl userFilmDaoImpl;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        userFilmDaoImpl = new UserFilmDaoImpl(jdbcTemplate);
    }

    @Test
    public void addLikeTest() {
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));
        Film film = new Film(1, "name", LocalDate.of(1967, 03, 25), "description", 100, new Mpa(1, "G"), genres);
        User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        FilmLike filmLike = new FilmLike(user.getId(), film.getId());

        jdbcTemplate.update("INSERT INTO FILMS(id, name, description, RELEASE_DATE, duration, mpa) VALUES (?,?,?,?,?,?)", film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        userFilmDaoImpl.addLike(film.getId(), user.getId());
        FilmLike savedFilmLike = jdbcTemplate.queryForObject("SELECT * FROM USER_FILM WHERE film_id = ?", getFilmLikeMapper(), film.getId());

        assertEquals(filmLike, savedFilmLike);
    }

    @Test
    public void deleteLike() {
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));
        Film film = new Film(1, "name", LocalDate.of(1967, 03, 25), "description", 100, new Mpa(1, "G"), genres);
        User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        jdbcTemplate.update("INSERT INTO FILMS(id, name, description, RELEASE_DATE, duration, mpa) VALUES (?,?,?,?,?,?)", film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        jdbcTemplate.update("INSERT INTO USER_FILM(user_id, film_id) VALUES(?, ?)", user.getId(), film.getId());

        userFilmDaoImpl.deleteLike(film.getId(), user.getId());
        assertThrows(EmptyResultDataAccessException.class, () -> jdbcTemplate.queryForObject("SELECT * FROM USER_FILM WHERE film_id = ?", getFilmLikeMapper(), film.getId()));
    }

    private RowMapper<FilmLike> getFilmLikeMapper() {
        return (rs, rowNum) -> new FilmLike(
                rs.getInt("user_id"),
                rs.getInt("film_id")
        );
    }
}
