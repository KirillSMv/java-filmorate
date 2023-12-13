package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    void deleteUserById(Integer id);

    User getUserById(Integer id);

    List<User> getUsers();
}
