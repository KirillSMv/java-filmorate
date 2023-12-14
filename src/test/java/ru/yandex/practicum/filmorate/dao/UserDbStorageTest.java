package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dao.userDao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private UserDbStorage filmDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        filmDbStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    public void testAddUser() {
        User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        filmDbStorage.addUser(user);

        User savedUser = jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", getUserMapper(), user.getId());

        assertEquals(user, savedUser);
    }

    @Test
    public void testGetUserById() {
        User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        User savedUser = filmDbStorage.getUserById(user.getId());

        assertEquals(user, savedUser);
    }

    @Test
    public void testUpdateUser() {
        User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        User newUser = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        newUser.setId(user.getId());

        filmDbStorage.updateUser(newUser);

        User savedUser = jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", getUserMapper(), newUser.getId());
        assertEquals(newUser, savedUser);
    }

    @Test
    public void testGetUsers() {
        List<User> users = new ArrayList<>();
        User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        User newUser = new User(2, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        users.add(user);
        users.add(newUser);
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", newUser.getId(), newUser.getEmail(), newUser.getLogin(), newUser.getName(), newUser.getBirthday());


        List<User> savedUsers = filmDbStorage.getUsers();

        assertEquals(users, savedUsers);
    }

    @Test
    public void deleteUserById() {
        User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        filmDbStorage.deleteUserById(user.getId());
        assertThrows(EmptyResultDataAccessException.class, () -> jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", getUserMapper(), user.getId()));
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
