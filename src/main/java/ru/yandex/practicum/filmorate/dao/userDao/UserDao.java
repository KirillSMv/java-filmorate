package ru.yandex.practicum.filmorate.dao.userDao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {
    User addUser(User user);

    User updateUser(User user);

    void deleteUserById(Integer id);

    User getUserById(Integer id);

    List<User> getUsers();

    void addToFriends(Integer id, Integer friendId);

    void deleteFriend(Integer id, Integer friendId);

    List<User> getFriends(Integer id);

    List<User> getCommonFriends(Integer id, Integer otherId);
}
