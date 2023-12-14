package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.userDao.UserFriendsDao;
import ru.yandex.practicum.filmorate.dao.userDao.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private UserStorage userStorage;
    private UserFriendsDao userFriends;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, UserFriendsDao userFriends) {
        this.userStorage = userStorage;
        this.userFriends = userFriends;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public void deleteUserById(Integer id) {
        userStorage.deleteUserById(id);
    }

    public void addToFriends(Integer id, Integer friendId) {
        userFriends.addToFriends(id, friendId);
    }

    public List<User> getFriends(Integer id) {
        return userFriends.getFriends(id);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return userFriends.getCommonFriends(id, otherId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        userFriends.deleteFriend(id, friendId);
    }
}
