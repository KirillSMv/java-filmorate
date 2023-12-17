package ru.yandex.practicum.filmorate.dao.userDao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.userDao.UserDao;
import ru.yandex.practicum.filmorate.exceptions.FriendshipException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository("UserDaoImpl")
public class UserDaoImpl implements UserDao {
    private JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
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
        if (checkIfUserExists(id)) {
            return jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", getUserMapper(), id);
        }
        log.error("Пользователь с id {} еще не добавлен.", id);
        throw new UserNotFoundException(String.format("Пользователь с id %d еще не добавлен.", id));
    }

    @Override
    public User updateUser(User user) {
        if (checkIfUserExists(user.getId())) {
            String sql = "UPDATE USERS SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
            jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
            return user;
        }
        log.error("Пользователь с id {} еще не добавлен.", user.getId());
        throw new UserNotFoundException(String.format("Пользователь с id %d еще не добавлен.", user.getId()));
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM USERS", getUserMapper());
    }

    @Override
    public void deleteUserById(Integer id) {
        if (checkIfUserExists(id)) {
            jdbcTemplate.update("DELETE FROM USERS WHERE id = ?", id);
        } else {
            log.error("Пользователь с id {} еще не добавлен.", id);
            throw new UserNotFoundException(String.format("Пользователь с id %d еще не добавлен.", id));
        }
    }

    @Override
    public void addToFriends(Integer userId, Integer friendId) {
        if (checkIfUserExists(userId) && checkIfUserExists(friendId)) {
            try {
                jdbcTemplate.update("INSERT INTO USER_FRIEND(user_id, friend_id) VALUES (?, ?)", userId, friendId);
            } catch (EmptyResultDataAccessException e) {
                log.error("Пользователь с id {} уже есть в друзьях у пользователя с id {}", friendId, userId);
                throw new FriendshipException("Пользователь с таким id не находится в друзьях с указанным пользователем");
            }
        } else {
            log.error("Неверно указаны id пользователей для добавления в друзья {}, {}", userId, friendId);
            throw new UserNotFoundException(String.format("Неверно указаны id пользователей для добавления в друзья %d, %d", userId, friendId));
        }
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        if (checkIfUserExists(userId) && checkIfUserExists(friendId)) {
            try {
                jdbcTemplate.update("DELETE FROM USER_FRIEND WHERE user_id = ? AND friend_id = ?", userId, friendId);
            } catch (EmptyResultDataAccessException e) {
                log.error("Пользователя с id {} нет в друзьях у пользователя с id {}", friendId, userId);
                throw new FriendshipException("Пользователь с таким id не находится в друзьях с указанным пользователем");
            }
        } else {
            log.error("Неверно указаны id пользователей для удаления из друзей {}, {}", userId, friendId);
            throw new UserNotFoundException(String.format("Неверно указаны id пользователей для удаления из друзей %d, %d", userId, friendId));
        }
    }

    @Override
    public List<User> getFriends(Integer userId) {
        if (checkIfUserExists(userId)) {
            return jdbcTemplate.query("SELECT * FROM USERS WHERE id IN (SELECT friend_id FROM USER_FRIEND WHERE user_id = ?)", getUserMapper(), userId);
        }
        log.error("Пользователь с id {} еще не добавлен.", userId);
        throw new UserNotFoundException(String.format("Пользователь с id %d еще не добавлен.", userId));
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        if (checkIfUserExists(userId) && checkIfUserExists(otherId)) {
            return jdbcTemplate.query("SELECT * FROM USERS WHERE id IN " +
                    "(SELECT friend_id FROM USER_FRIEND WHERE user_id = ? AND friend_id IN " +
                    "(SELECT friend_id FROM USER_FRIEND WHERE user_id = ?))", getUserMapper(), userId, otherId);
        } else {
            log.error("Неверно указаны id пользователей для отображения общих друзей {}, {}", userId, otherId);
            throw new UserNotFoundException(String.format("Неверно указаны id пользователей для отображения общих друзей %d, %d", userId, otherId));
        }
    }

    public RowMapper<User> getUserMapper() {
        return (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }

    private Map<String, Object> userToMap(User user) {
        return Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday()
        );
    }

    private boolean checkIfUserExists(Integer id) {
        Integer result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USERS WHERE id = ?", Integer.class, id);
        if (result == 0) {
            log.error("Пользователя с таким id {} нет", id);
            return false;
        }
        return true;
    }
}
