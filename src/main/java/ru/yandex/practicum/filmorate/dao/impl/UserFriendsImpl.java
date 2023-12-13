package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserFriendsDao;
import ru.yandex.practicum.filmorate.exceptions.FriendshipException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;

import java.util.List;

@Repository
public class UserFriendsImpl implements UserFriendsDao {
    private JdbcTemplate jdbcTemplate;

    public UserFriendsImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addToFriends(Integer id, Integer friendId) {
        try {
            int result = jdbcTemplate.update("INSERT INTO USER_FRIEND(user_id, friend_id) VALUES (?, ?)", id, friendId);
        } catch (DataAccessException e) {
            //log.error("Пользователь с id {} уже есть в друзьях у пользователя с id {}", friendId, id);
            throw new ObjectNotFoundException("Пользователь с таким id не находится в друзьях с указанным пользователем");
        }
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        int result = jdbcTemplate.update("DELETE FROM USER_FRIEND WHERE user_id = ? AND friend_id = ?", id, friendId);
        if (result == 0) {
            throw new FriendshipException(String.format("Пользователь")); //todo
        }
    }

    @Override
    public List<User> getFriends(Integer id) {
        List<User> users = null;
        try {
            users = jdbcTemplate.query("SELECT * FROM USERS WHERE id IN (SELECT friend_id FROM USER_FRIEND WHERE user_id = ?)", getUserMapper(), id);
        } catch (DataAccessException e) {
            throw new FriendshipException(String.format("Пользователь")); //todo
        }
        return users;
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<User> idFriends = null;
        try {
            idFriends = jdbcTemplate.query("SELECT * FROM USERS WHERE id IN " +
                    "(SELECT friend_id FROM USER_FRIEND WHERE user_id = ? AND friend_id IN " +
                    "(SELECT friend_id FROM USER_FRIEND WHERE user_id = ?))", getUserMapper(), id, otherId);

        } catch (DataAccessException e) {
            throw new FriendshipException(String.format("Пользователь")); //todo
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
}
