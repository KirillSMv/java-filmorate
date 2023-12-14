package ru.yandex.practicum.filmorate.dao.userDao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.userDao.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

@Slf4j
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

    @Override
    public User getUserById(Integer id) {
        checkIfUserExists(id);
        return jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", getUserMapper(), id);
    }

    @Override
    public User updateUser(User user) {
        checkIfUserExists(user.getId());
        String sql = "UPDATE USERS SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM USERS", getUserMapper());
    }

    @Override
    public void deleteUserById(Integer id) {
        checkIfUserExists(id);
        jdbcTemplate.update("DELETE FROM USERS WHERE id = ?", id);
    }

    public void checkIfUserExists(Integer id) {
        try {
            jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", getUserMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Пользователя с таким id {} нет", id);
            throw new UserNotFoundException(String.format("Пользователя с таким id %d нет", id));
        }
    }

    private Map<String, Object> userToMap(User user) {
        return Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday()
        );
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
