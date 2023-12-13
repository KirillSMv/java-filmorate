package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmLikesStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class FilmLikesDbStorage implements FilmLikesStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmLikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        int result = jdbcTemplate.update("INSERT INTO USER_FILM(user_id, film_id) VALUES(?, ?);", userId, id);
        if (result == 0) {
            //log.error("Пользователь с id {} уже ставил лайк фильму с id {}", userId, id);
            throw new FilmNotFoundException("Неверный запрос, проверьте данные");
        }
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        int result = jdbcTemplate.update("DELETE FROM USER_FILM WHERE user_id = ? AND film_id = ?", userId, id);
        if (result == 0) {
            throw new FilmNotFoundException(String.format("Неверно указаны данные для удаления лайка", userId, id));
        }
    }

    @Override
    public List<Film> getPopularFilms() {
        return jdbcTemplate.query("SELECT * FROM FILMS WHERE id IN (SELECT film_id FROM USER_FILM GROUP BY film_id ORDER BY COUNT(user_id) DESC)", getFilmMapper());
    }

    @Override
    public List<Film> getNotPopularFilms() {
        return jdbcTemplate.query("SELECT * FROM FILMS WHERE id NOT IN(SELECT film_id FROM USER_FILM GROUP BY film_id ORDER BY COUNT(user_id) DESC)", getFilmMapper());
    }

    @Override
    public FilmLikes getRating(Film film) {
        return jdbcTemplate.queryForObject("SELECT COUNT(user_id), film_id FROM USER_FILM WHERE film_id = ?", getFilmLikes(), film.getId());
    }

    private RowMapper<FilmLikes> getFilmLikes() {
        return (rs, rowNum) -> new FilmLikes(
                rs.getInt("COUNT(user_id)"),
                rs.getInt("film_id")
        );
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
        return new HashSet<>(jdbcTemplate.query(
                "SELECT * FROM GENRE WHERE GENRE_ID IN (" +
                        "SELECT GENRE_ID " +
                        "FROM FILM_GENRE WHERE film_id = ?)", new GenresMapper(), id)); //todo
    }

    private Mpa createMpa(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE id = ?", new MpaMapper(), id);
    }
}
