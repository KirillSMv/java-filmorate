package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dao.userDao.impl.UserFriendsImpl;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserFriendsImplTest {
    private UserFriendsImpl userFriendsImpl;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        userFriendsImpl = new UserFriendsImpl(jdbcTemplate);
    }

    @Test
    public void testAddToFriends() {
        User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        User newUser = new User(2, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", newUser.getId(), newUser.getEmail(), newUser.getLogin(), newUser.getName(), newUser.getBirthday());
        UserFriend userFriend = new UserFriend(user.getId(), newUser.getId());
        userFriendsImpl.addToFriends(user.getId(), newUser.getId());

        UserFriend savedUserFriend = jdbcTemplate.queryForObject("SELECT * FROM USER_FRIEND WHERE user_id = ? AND friend_id = ?", getUserFriendMapper(), user.getId(), newUser.getId());

        assertEquals(userFriend, savedUserFriend);
    }

    @Test
    public void testDeleteFriend() {
        User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        User newUser = new User(2, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", newUser.getId(), newUser.getEmail(), newUser.getLogin(), newUser.getName(), newUser.getBirthday());
        jdbcTemplate.update("INSERT INTO USER_FRIEND(user_id, friend_id) VALUES (?, ?)", user.getId(), newUser.getId());

        userFriendsImpl.deleteFriend(user.getId(), newUser.getId());
        assertThrows(EmptyResultDataAccessException.class, () -> jdbcTemplate.queryForObject("SELECT * FROM USER_FRIEND WHERE user_id = ? AND friend_id = ?", getUserMapper(), user.getId(), newUser.getId()));
    }

    @Test
    public void testGetFriends() {
        List<User> friends = new ArrayList<>();
        User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        User newUser = new User(2, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        User newUser2 = new User(3, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", newUser.getId(), newUser.getEmail(), newUser.getLogin(), newUser.getName(), newUser.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", newUser2.getId(), newUser2.getEmail(), newUser2.getLogin(), newUser2.getName(), newUser2.getBirthday());

        jdbcTemplate.update("INSERT INTO USER_FRIEND(user_id, friend_id) VALUES (?, ?)", user.getId(), newUser.getId());
        jdbcTemplate.update("INSERT INTO USER_FRIEND(user_id, friend_id) VALUES (?, ?)", user.getId(), newUser2.getId());
        friends.add(newUser);
        friends.add(newUser2);

        List<User> savedFriends = userFriendsImpl.getFriends(user.getId());

        assertEquals(friends, savedFriends);
    }

    @Test
    public void testGetCommonFriends() {
        List<User> friends = new ArrayList<>();
        User user = new User(1, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        User newUser = new User(2, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        User newUser2 = new User(3, "mail@mail.ru", "dolore", "name", LocalDate.of(1946, 8, 20));
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", newUser.getId(), newUser.getEmail(), newUser.getLogin(), newUser.getName(), newUser.getBirthday());
        jdbcTemplate.update("INSERT INTO USERS(id, email, login, name, birthday) VALUES (?,?,?,?, ?)", newUser2.getId(), newUser2.getEmail(), newUser2.getLogin(), newUser2.getName(), newUser2.getBirthday());

        jdbcTemplate.update("INSERT INTO USER_FRIEND(user_id, friend_id) VALUES (?, ?)", user.getId(), newUser2.getId());
        jdbcTemplate.update("INSERT INTO USER_FRIEND(user_id, friend_id) VALUES (?, ?)", newUser.getId(), newUser2.getId());
        friends.add(newUser2);
        List<User> commonFriends = userFriendsImpl.getCommonFriends(user.getId(), newUser.getId());

        assertEquals(friends, commonFriends);
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

    private RowMapper<UserFriend> getUserFriendMapper() {
        return (rs, rowNum) -> new UserFriend(
                rs.getInt("user_id"),
                rs.getInt("friend_id")
        );
    }
}
