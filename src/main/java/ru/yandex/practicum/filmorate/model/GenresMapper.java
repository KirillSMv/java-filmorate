package ru.yandex.practicum.filmorate.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenresMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre"));
    }
}
