package ru.yandex.practicum.filmorate.dao.filmDao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.filmDao.MpaDao;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Repository
public class MpaDaoImpl implements MpaDao {
    private JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpa() {
        return jdbcTemplate.query("SELECT * FROM MPA", getMpaMapper());
    }

    @Override
    public Mpa getMpaById(Integer id) {
        Mpa mpa = null;
        try {
            mpa = jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE id = ?", getMpaMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Рейтинга с id {} еще нет.", id);
            throw new ObjectNotFoundException("Такого рейтинга нет");
        }
        return mpa;
    }

    public Mpa createMpa(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE id = ?", getMpaMapper(), id);
    }

    private RowMapper<Mpa> getMpaMapper() {
        return (rs, rowNum) -> new Mpa(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
