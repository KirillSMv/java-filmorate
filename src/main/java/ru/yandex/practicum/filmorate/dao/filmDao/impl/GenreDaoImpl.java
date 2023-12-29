package ru.yandex.practicum.filmorate.dao.filmDao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.filmDao.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Repository
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query("SELECT * FROM GENRE", getGenreMapper());
    }

    @Override
    public Genre getGenreById(Integer id) {
        if (checkIfGenreExists(id)) {
            return jdbcTemplate.queryForObject("SELECT * FROM GENRE WHERE genre_id = ?", getGenreMapper(), id);
        }
        log.error("Жанр с id {} еще не добавлен.", id);
        throw new ObjectNotFoundException(String.format("Жанр с id %d еще не добавлен.", id));
    }

    private RowMapper<Genre> getGenreMapper() {
        return (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre")
        );
    }

    private boolean checkIfGenreExists(Integer id) {
        Integer result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM GENRE WHERE genre_id = ?", Integer.class, id);
        if (result == 0) {
            log.error("Жанра с таким id {} нет", id);
            return false;
        }
        return true;
    }
}
