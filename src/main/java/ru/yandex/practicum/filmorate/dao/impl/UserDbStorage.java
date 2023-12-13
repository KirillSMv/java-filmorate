package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

@Repository("userDbStorage")
public class UserDbStorage implements UserStorage {
    private JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("id");
        int id = insert.executeAndReturnKey(userToMap(user)).intValue();
        user.setId(id);
        return user;
    }

    private Map<String, Object> userToMap(User user) {
        return Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday()
        );
    }

    @Override
    public User getUserById(Integer id) {
        User user = null;
        try {
            user = jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", getUserMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Такого пользователя нет");
        }
        return user;
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

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE USERS SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        int result = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (result == 0) {
            //log.error("такой пользователь уже добавлен");
            throw new UserNotFoundException("такой пользователь не добавлен");
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM USERS", getUserMapper());
    }

    @Override
    public void deleteUserById(Integer id) {
        jdbcTemplate.update("DELETE FROM USERS WHERE id = ?", id);
    }
}
