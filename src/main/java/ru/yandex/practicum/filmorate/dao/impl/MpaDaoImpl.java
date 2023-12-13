package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

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
            throw new ObjectNotFoundException("Такого рейтинга нет");
        }
        return mpa;
    }

    private RowMapper<Mpa> getMpaMapper() {
        return (rs, rowNum) -> new Mpa(
                rs.getInt("id"),
                rs.getString("name")
        );
    }

}
