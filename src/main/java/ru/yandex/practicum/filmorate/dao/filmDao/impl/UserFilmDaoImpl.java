package ru.yandex.practicum.filmorate.dao.filmDao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.filmDao.UserFilmDao;
import ru.yandex.practicum.filmorate.exceptions.FilmLikesException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
public class UserFilmDaoImpl implements UserFilmDao {
    private final JdbcTemplate jdbcTemplate;

    public UserFilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        checkIfFilmExists(id);
        checkIfUserExists(userId);
        try {
            jdbcTemplate.update("INSERT INTO USER_FILM(user_id, film_id) VALUES(?, ?);", userId, id);
        } catch (DataAccessException e) {
            log.error("Пользователь с id {} уже ставил лайк фильму с id {}", userId, id);
            throw new FilmLikesException(String.format("Пользователь с id %d уже ставил лайк фильму с id %d", userId, id));
        }
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        checkIfFilmExists(id);
        checkIfUserExists(userId);
        try {
            jdbcTemplate.update("DELETE FROM USER_FILM WHERE user_id = ? AND film_id = ?", userId, id);
        } catch (DataAccessException e) {
            log.error("Неверно указаны данные для удаления лайка: userId {}, id {}", userId, id);
            throw new FilmLikesException(String.format("Неверно указаны данные для удаления лайка: userId %d, id %d", userId, id));
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

    private void checkIfFilmExists(Integer id) {
        try {
            jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE id = ?", getFilmMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("фильм с id {} еще не добавлен.", id);
            throw new FilmNotFoundException(String.format("фильм с id %d еще не добавлен.", id));
        }
    }

    public void checkIfUserExists(Integer id) {
        try {
            jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", getUserMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Пользователя с таким id {} нет", id);
            throw new UserNotFoundException(String.format("Пользователя с таким id %d нет", id));
        }
    }

    private RowMapper<User> getUserMapper() {
        return (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }
}
