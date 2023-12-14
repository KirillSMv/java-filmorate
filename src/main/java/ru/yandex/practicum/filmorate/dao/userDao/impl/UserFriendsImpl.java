package ru.yandex.practicum.filmorate.dao.userDao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.userDao.UserFriendsDao;
import ru.yandex.practicum.filmorate.exceptions.FriendshipException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;

import java.util.List;

@Slf4j
@Repository
public class UserFriendsImpl implements UserFriendsDao {
    private final JdbcTemplate jdbcTemplate;

    public UserFriendsImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addToFriends(Integer id, Integer friendId) {
        checkIfUserExists(id);
        checkIfUserExists(friendId);
        try {
            jdbcTemplate.update("INSERT INTO USER_FRIEND(user_id, friend_id) VALUES (?, ?)", id, friendId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Пользователь с id {} уже есть в друзьях у пользователя с id {}", friendId, id);
            throw new FriendshipException("Пользователь с таким id не находится в друзьях с указанным пользователем");
        }
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        checkIfUserExists(id);
        checkIfUserExists(friendId);
        try {
            jdbcTemplate.update("DELETE FROM USER_FRIEND WHERE user_id = ? AND friend_id = ?", id, friendId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Пользователь с id {} уже есть в друзьях у пользователя с id {}", friendId, id);
            throw new FriendshipException(String.format("Пользователи не являются друзьями"));
        }
    }

    @Override
    public List<User> getFriends(Integer id) {
        checkIfUserExists(id);

        return jdbcTemplate.query("SELECT * FROM USERS WHERE id IN (SELECT friend_id FROM USER_FRIEND WHERE user_id = ?)", getUserMapper(), id);
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        checkIfUserExists(id);
        checkIfUserExists(otherId);
        List<User> idFriends = null;
        try {
            idFriends = jdbcTemplate.query("SELECT * FROM USERS WHERE id IN " +
                    "(SELECT friend_id FROM USER_FRIEND WHERE user_id = ? AND friend_id IN " +
                    "(SELECT friend_id FROM USER_FRIEND WHERE user_id = ?))", getUserMapper(), id, otherId);

        } catch (EmptyResultDataAccessException e) {
            log.error("У пользователей нет общих друзей");
            throw new FriendshipException(String.format("У пользователей нет общих друзей"));
        }
        return idFriends;
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

    public void checkIfUserExists(Integer id) {
        try {
            jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", getUserMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Пользователя с таким id {} нет", id);
            throw new UserNotFoundException(String.format("Пользователя с таким id %d нет", id));
        }
    }
}
