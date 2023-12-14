package ru.yandex.practicum.filmorate.dao.filmDao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.filmDao.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
public class GenreDaoImpl implements GenreDao {
    private JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query("SELECT * FROM GENRE", getGenreMapper());
    }

    @Override
    public Genre getGenreById(Integer id) {
        Genre genre = null;
        try {
            genre = jdbcTemplate.queryForObject("SELECT * FROM GENRE WHERE genre_id = ?", getGenreMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Такого жанра нет");
        }
        return genre;
    }

    private Set<Genre> createGenres(int id) {
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
