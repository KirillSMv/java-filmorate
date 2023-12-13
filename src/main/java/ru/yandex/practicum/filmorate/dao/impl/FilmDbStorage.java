package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("id");
        int id = insert.executeAndReturnKey(filmToMap(film)).intValue();
        film.setId(id);
        saveGenres(film);
        if (film.getGenres() != null) {
            sortGenres(film);
        }
        return film;
    }

    @Override
    public Film getFilmById(Integer id) {
        Film film = null;
        try {
            film = jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE id = ?", getFilmMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException("Такого фильма нет");
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        try {
            jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE id = ?", getFilmMapper(), film.getId());
        } catch (DataAccessException e) {
            throw new FilmNotFoundException(String.format("фильм с id %d еще не добавлен.", film.getId())); //выбирасывает ошибку 500
        }

        String sql = "UPDATE FILMS SET name = ?, release_date = ?, description = ?, duration = ?, mpa = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getReleaseDate(), film.getDescription(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        removeGenres(film.getId());
        saveGenres(film);
        if (film.getGenres() != null) {
            sortGenres(film);
        }
        return film;
    }

    @Override
    public void deleteFilmById(Integer id) {
        jdbcTemplate.update("DELETE FROM FILMS WHERE id = ?", id);
    }

    @Override
    public List<Film> getFilms() {
        return jdbcTemplate.query("SELECT * FROM FILMS", getFilmMapper());
    }

    private void sortGenres(Film film) {
        HashSet<Genre> sortedHashSet = film.getGenres().stream()
                .sorted(this::compare)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        film.setGenres(sortedHashSet);
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

    private Set<Genre> createGenres(int id) {
        Set<Genre> set = new HashSet<>(jdbcTemplate.query(
                "SELECT * FROM GENRE WHERE GENRE_ID IN (" +
                        "SELECT GENRE_ID " +
                        "FROM FILM_GENRE WHERE film_id = ?)", new GenresMapper(), id)); //todo
        return set.stream()
                .sorted(this::compare)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Mpa createMpa(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE id = ?", new MpaMapper(), id);
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

    private void removeGenres(int id) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, id);
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

    private int compare(Genre genre1, Genre genre2) {
        return Integer.compare(genre1.getId(), genre2.getId());
    }
}
