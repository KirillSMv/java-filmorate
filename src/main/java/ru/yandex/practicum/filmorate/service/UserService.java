package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.userDao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private UserDao userDao;

    @Autowired
    public UserService(@Qualifier("UserDaoImpl") UserDao userDao) {
        this.userDao = userDao;
    }

    public User addUser(User user) {
        return userDao.addUser(user);
    }

    public User updateUser(User user) {
        return userDao.updateUser(user);
    }

    public List<User> getUsers() {
        return userDao.getUsers();
    }

    public User getUserById(Integer id) {
        return userDao.getUserById(id);
    }

    public void deleteUserById(Integer id) {
        userDao.deleteUserById(id);
    }

    public void addToFriends(Integer id, Integer friendId) {
        userDao.addToFriends(id, friendId);
    }

    public List<User> getFriends(Integer id) {
        return userDao.getFriends(id);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return userDao.getCommonFriends(id, otherId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        userDao.deleteFriend(id, friendId);
    }
}
