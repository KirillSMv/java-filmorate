package ru.yandex.practicum.filmorate.dao.filmDao.impl;

import lombok.extern.slf4j.Slf4j;
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
    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpa() {
        return jdbcTemplate.query("SELECT * FROM MPA", getMpaMapper());
    }

    public RowMapper<Mpa> getMpaMapper() {
        return (rs, rowNum) -> new Mpa(
                rs.getInt("id"),
                rs.getString("name")
        );
    }

    @Override
    public Mpa getMpaById(Integer id) {
        if (checkIfMpaExists(id)) {
            return jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE id = ?", getMpaMapper(), id);
        }
        log.error("Рейтинг с id {} еще не добавлен.", id);
        throw new ObjectNotFoundException(String.format("Рейтинг с id {} еще не добавлен.", id));
    }

    private boolean checkIfMpaExists(Integer id) {
        Integer result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM MPA WHERE id = ?", Integer.class, id);
        if (result == 0) {
            log.error("Рейтинга с id {} нет", id);
            return false;
        }
        return true;
    }
}
