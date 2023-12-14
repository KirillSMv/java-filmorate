package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import ru.yandex.practicum.filmorate.dao.filmDao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class FilmDbStorageTest {
    private FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
    }

    @Test
    public void testAddFilm() {
        Set<Genre> genres = new HashSet<>();
        Film film = new Film(1, "name", LocalDate.of(1967, 03, 25), "description", 100, new Mpa(1, "G"), genres);
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));
        filmDbStorage = new FilmDbStorage(jdbcTemplate);

        filmDbStorage.addFilm(film);
        Film savedFilm = jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE id = ?", getFilmMapper(), film.getId());

        assertEquals(savedFilm, film);
    }

    @Test
    public void testGetFilmById() {
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));
        Film film = new Film(1, "name", LocalDate.of(1967, 03, 25), "description", 100, new Mpa(1, "G"), genres);
        saveFilm(film);
        Film savedFilm = filmDbStorage.getFilmById(film.getId());
        assertEquals(film, savedFilm);
    }

    @Test
    public void testUpdateFilm() {
        Set<Genre> genres = new HashSet<>();
        Film film = new Film(1, "name", LocalDate.of(1967, 03, 25), "description", 100, new Mpa(1, "G"), genres);
        Film newFilm = new Film(1, "newName", LocalDate.of(1967, 03, 25), "newDescription", 100, new Mpa(1, "G"), genres);

        saveFilm(film);
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));
        newFilm.setId(film.getId());
        filmDbStorage.updateFilm(newFilm);

        Film savedFilm = jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE id = ?", getFilmMapper(), newFilm.getId());

        assertEquals(savedFilm, newFilm);
    }

    @Test
    public void testDeleteFilmById() {
        Set<Genre> genres = new HashSet<>();
        Film film = new Film(1, "name", LocalDate.of(1967, 03, 25), "description", 100, new Mpa(1, "G"), genres);

        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));
        saveFilm(film);
        filmDbStorage.deleteFilmById(film.getId());
        assertThrows(EmptyResultDataAccessException.class, () -> jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE id = ?", getFilmMapper(), 1));
    }

    @Test
    public void testGetFilms() {
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));

        Film film = new Film(1, "name", LocalDate.of(1967, 03, 25), "description", 100, new Mpa(1, "G"), genres);
        Film newFilm = new Film(1, "newName", LocalDate.of(1967, 03, 25), "newDescription", 100, new Mpa(1, "G"), genres);

        List<Film> films = new ArrayList<>();
        films.add(film);
        films.add(newFilm);

        saveFilm(film);
        newFilm.setId(2);
        saveFilm(newFilm);

        List<Film> savedFilmes = filmDbStorage.getFilms();

        assertEquals(films, savedFilmes);
    }


    private void saveFilm(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("id");
        int id = insert.executeAndReturnKey(filmToMap(film)).intValue();
        film.setId(id);
        saveGenres(film);
        if (film.getGenres() != null) {
            sortGenres(film);
        }
    }

    private void sortGenres(Film film) {
        HashSet<Genre> sortedHashSet = film.getGenres().stream()
                .sorted(this::compare)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        film.setGenres(sortedHashSet);
    }

    private int compare(Genre genre1, Genre genre2) {
        return Integer.compare(genre1.getId(), genre2.getId());
    }

    private Map<String, Object> filmToMap(Film film) {
        return Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "RELEASE_DATE", film.getReleaseDate(),
                "duration", film.getDuration(),
                "mpa", film.getMpa().getId()
        );
    }

    public void saveGenres(Film film) {
        if (film.getGenres() != null) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            jdbcTemplate.batchUpdate("INSERT INTO FILM_GENRE(film_id, genre_id) VALUES (?, ?);",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, film.getId());
                            ps.setInt(2, genres.get(i).getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return genres.size();
                        }
                    }
            );
        }
    }

    private RowMapper<Film> getFilmMapper() {
        return (rs, rowNum) -> new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getString("description"),
                rs.getInt("duration"),
                createMpa(rs.getInt("mpa")),
                createGenres(rs.getInt("id"))
        );
    }

    private Mpa createMpa(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE id = ?", getMpaMapper(), id);
    }

    private RowMapper<Mpa> getMpaMapper() {
        return (rs, rowNum) -> new Mpa(
                rs.getInt("id"),
                rs.getString("name")
        );
    }

    public Set<Genre> createGenres(int id) {
        return new HashSet<>(jdbcTemplate.query(
                "SELECT * FROM GENRE WHERE GENRE_ID IN (" +
                        "SELECT GENRE_ID " +
                        "FROM FILM_GENRE WHERE film_id = ?)", getGenreMapper(), id));
    }

    private RowMapper<Genre> getGenreMapper() {
        return (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre")
        );
    }
}
