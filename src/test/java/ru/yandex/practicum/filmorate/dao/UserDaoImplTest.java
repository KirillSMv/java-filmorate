package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.userDao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDaoImplTest {
    private UserDaoImpl filmDbStorage;
    private final JdbcTemplate jdbcTemplate;
    User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
    User otherUser = new User(2, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));

    @BeforeEach
    public void setUp() {
        filmDbStorage = new UserDaoImpl(jdbcTemplate);
    }

    @Test
    public void testAddUser() {
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        User savedUser = jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", filmDbStorage.getUserMapper(), user.getId());
        assertEquals(user, savedUser);
    }

    @Test
    public void testGetUserById() {
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        User savedUser = filmDbStorage.getUserById(user.getId());
        assertEquals(user, savedUser);
    }

    @Test
    public void testUpdateUser() {
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        otherUser.setId(user.getId());
        filmDbStorage.updateUser(otherUser);

        User savedUser = jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", filmDbStorage.getUserMapper(), otherUser.getId());
        assertEquals(otherUser, savedUser);
    }

    @Test
    public void testGetUsers() {
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(otherUser);
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", otherUser.getId(), otherUser.getEmail(), otherUser.getLogin(), otherUser.getName(), otherUser.getBirthday());

        List<User> savedUsers = filmDbStorage.getUsers();
        assertEquals(users, savedUsers);
    }

    @Test
    public void deleteUserById() {
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        filmDbStorage.deleteUserById(user.getId());
        assertThrows(EmptyResultDataAccessException.class, () -> jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", filmDbStorage.getUserMapper(), user.getId()));
    }

    @Test
    public void testAddToFriends() {
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", otherUser.getId(), otherUser.getEmail(), otherUser.getLogin(), otherUser.getName(), otherUser.getBirthday());
        filmDbStorage.addToFriends(user.getId(), otherUser.getId());

        User friend = jdbcTemplate.queryForObject("SELECT * FROM USERS AS u JOIN USER_FRIEND AS uf ON u.id = uf.user_id WHERE friend_id = ?", filmDbStorage.getUserMapper(), otherUser.getId());
        assertEquals(user, friend);
    }

    @Test
    public void testDeleteFriend() {
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", otherUser.getId(), otherUser.getEmail(), otherUser.getLogin(), otherUser.getName(), otherUser.getBirthday());
        jdbcTemplate.update("INSERT INTO USER_FRIEND(user_id, friend_id) VALUES (?, ?)", user.getId(), otherUser.getId());

        filmDbStorage.deleteFriend(user.getId(), otherUser.getId());
        assertThrows(EmptyResultDataAccessException.class, () -> jdbcTemplate.queryForObject("SELECT * FROM USERS AS u JOIN USER_FRIEND AS uf ON u.id = uf.user_id WHERE friend_id = ?", filmDbStorage.getUserMapper(), otherUser.getId()));
    }

    @Test
    public void testGetFriends() {
        List<User> friends = new ArrayList<>();
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", otherUser.getId(), otherUser.getEmail(), otherUser.getLogin(), otherUser.getName(), otherUser.getBirthday());

        jdbcTemplate.update("INSERT INTO USER_FRIEND(user_id, friend_id) VALUES (?, ?)", user.getId(), otherUser.getId());
        friends.add(otherUser);

        List<User> savedFriends = filmDbStorage.getFriends(user.getId());
        assertEquals(friends, savedFriends);
    }

    @Test
    public void testGetCommonFriends() {
        List<User> friends = new ArrayList<>();
        User newUser2 = new User(3, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", otherUser.getId(), otherUser.getEmail(), otherUser.getLogin(), otherUser.getName(), otherUser.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", newUser2.getId(), newUser2.getEmail(), newUser2.getLogin(), newUser2.getName(), newUser2.getBirthday());

        jdbcTemplate.update("INSERT INTO USER_FRIEND(user_id, friend_id) VALUES (?, ?)", user.getId(), newUser2.getId());
        jdbcTemplate.update("INSERT INTO USER_FRIEND(user_id, friend_id) VALUES (?, ?)", otherUser.getId(), newUser2.getId());
        friends.add(newUser2);
        List<User> commonFriends = filmDbStorage.getCommonFriends(user.getId(), otherUser.getId());
        assertEquals(friends, commonFriends);
    }

}
