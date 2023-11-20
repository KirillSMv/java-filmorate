package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FriendshipException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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
        User user = userStorage.getUserById(id);
        User friendUser = userStorage.getUserById(friendId);

        boolean isFriendAdded = user.addToFriends(friendId);
        if (!isFriendAdded) {
            log.error("Пользователь с id {} уже есть в друзьях у пользователя с id {}", friendId, id);
            throw new FriendshipException(String.format("Пользователь с id %d уже есть в друзьях у пользователя с id %d", friendId, id));
        }
        friendUser.addToFriends(id);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        User user = userStorage.getUserById(id);
        User friendUser = userStorage.getUserById(friendId);

        if (!user.checkIfFriends(friendId)) {
            log.error("Пользователя с id {} нет в друзьях у пользователя с id {}", friendId, id);
            throw new FriendshipException(String.format("Пользователя с id %d нет в друзьях у пользователя с id %d", friendId, id));
        }
        user.deleteFriend(friendId);
        friendUser.deleteFriend(id);
    }

    public List<User> getFriends(Integer id) {
        return userStorage.getUserById(id).getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        Set<Integer> otherIdFriends = userStorage.getUserById(otherId).getFriends();
        Set<Integer> userIdFriends = userStorage.getUserById(id).getFriends();
        if (otherIdFriends == null || userIdFriends == null) {
            return new ArrayList<User>();
        }
        List<User> friends = userIdFriends.stream()
                .filter(otherIdFriends::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
        return friends;
    }
}
