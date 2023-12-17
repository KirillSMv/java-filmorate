package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.filmDao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.filmDao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class FilmDaoImplTest {
    private FilmDaoImpl filmDbStorage;
    private final JdbcTemplate jdbcTemplate;
    private MpaDaoImpl mpaDaoImpl;
    Film film = new Film(1, "name", LocalDate.of(1967, 03, 25), "description", 100, new Mpa(1, "G"), genresForTest);
    Film otherFilm = new Film(2, "newName", LocalDate.of(1967, 03, 25), "newDescription", 100, new Mpa(1, "G"), genresForTest);
    User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
    static Set<Genre> genresForTest = new HashSet<>();

    {
        genresForTest.add(new Genre(1, "Комедия"));
        genresForTest.add(new Genre(2, "Драма"));
    }

    @BeforeEach
    public void setUp() {
        filmDbStorage = new FilmDaoImpl(jdbcTemplate, new MpaDaoImpl(jdbcTemplate));
    }

    @Test
    public void testAddFilm() {
        jdbcTemplate.update("INSERT INTO FILMS(id, name, description, release_date, duration, mpa) VALUES(?,?,?,?,?,?)",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        filmDbStorage.saveGenres(film);

        Film savedFilm = jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE id = ?", filmDbStorage.getFilmMapper(), film.getId());
        assertEquals(savedFilm, film);
    }

    @Test
    public void testGetFilmById() {
        jdbcTemplate.update("INSERT INTO FILMS(id, name, description, release_date, duration, mpa) VALUES(?,?,?,?,?,?)",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        filmDbStorage.saveGenres(film);

        Film savedFilm = filmDbStorage.getFilmById(film.getId());
        assertEquals(film, savedFilm);
    }

    @Test
    public void testUpdateFilm() {
        jdbcTemplate.update("INSERT INTO FILMS(id, name, description, release_date, duration, mpa) VALUES(?,?,?,?,?,?)",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        filmDbStorage.saveGenres(film);
        otherFilm.setId(film.getId());
        filmDbStorage.updateFilm(otherFilm);

        Film savedFilm = jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE id = ?", filmDbStorage.getFilmMapper(), otherFilm.getId());
        assertEquals(savedFilm, otherFilm);
    }

    @Test
    public void testDeleteFilmById() {
        jdbcTemplate.update("INSERT INTO FILMS(id, name, description, release_date, duration, mpa) VALUES(?,?,?,?,?,?)",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        filmDbStorage.saveGenres(film);
        filmDbStorage.deleteFilmById(film.getId());
        assertThrows(EmptyResultDataAccessException.class, () -> jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE id = ?", filmDbStorage.getFilmMapper(), 1));
    }

    @Test
    public void testGetFilms() {
        List<Film> films = new ArrayList<>();

        jdbcTemplate.update("INSERT INTO FILMS(id, name, description, release_date, duration, mpa) VALUES(?,?,?,?,?,?)",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        filmDbStorage.saveGenres(film);

        jdbcTemplate.update("INSERT INTO FILMS(id, name, description, release_date, duration, mpa) VALUES(?,?,?,?,?,?)",
                otherFilm.getId(), otherFilm.getName(), otherFilm.getDescription(), otherFilm.getReleaseDate(), otherFilm.getDuration(), otherFilm.getMpa().getId());
        filmDbStorage.saveGenres(otherFilm);
        films.add(film);
        films.add(otherFilm);

        List<Film> savedFilmes = filmDbStorage.getFilms();
        assertEquals(films, savedFilmes);
    }

    @Test
    public void addLikeTest() {
        jdbcTemplate.update("INSERT INTO FILMS(id, name, description, RELEASE_DATE, duration, mpa) VALUES (?,?,?,?,?,?)", film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        filmDbStorage.addLike(film.getId(), user.getId());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USER_FILM WHERE film_id = ?", Integer.class, film.getId());
        assertEquals(1, count);
    }

    @Test
    public void deleteLike() {
        jdbcTemplate.update("INSERT INTO FILMS(id, name, description, RELEASE_DATE, duration, mpa) VALUES (?,?,?,?,?,?)", film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        jdbcTemplate.update("INSERT INTO USER_FILM(user_id, film_id) VALUES(?, ?)", user.getId(), film.getId());

        filmDbStorage.deleteLike(film.getId(), user.getId());
        assertEquals(0, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USER_FILM WHERE film_id = ?", Integer.class, film.getId()));
    }
}

