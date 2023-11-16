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
    private final UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addUser(User user) {
        return inMemoryUserStorage.addUser(user);
    }

    public User updateUser(User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    public User getUserById(Integer id) {
        return inMemoryUserStorage.getUserById(id);
    }

    public void deleteUserById(Integer id) {
        inMemoryUserStorage.deleteUserById(id);
    }

    public void addToFriends(Integer id, Integer friendId) {
        User user = inMemoryUserStorage.getUserById(id);
        User friendUser = inMemoryUserStorage.getUserById(friendId);

        boolean isFriendAdded = user.addToFriends(friendId);
        if (!isFriendAdded) {
            log.error("Пользователь с id {} уже есть в друзьях у пользователя с id {}", friendId, id);
            throw new FriendshipException(String.format("Пользователь с id %d уже есть в друзьях у пользователя с id %d", friendId, id));
        }
        friendUser.addToFriends(id);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        User user = inMemoryUserStorage.getUserById(id);
        User friendUser = inMemoryUserStorage.getUserById(friendId);

        if (!user.checkIfFriends(friendId)) {
            log.error("Пользователя с id {} нет в друзьях у пользователя с id {}", friendId, id);
            throw new FriendshipException(String.format("Пользователя с id %d нет в друзьях у пользователя с id %d", friendId, id));
        }
        user.deleteFriend(friendId);
        friendUser.deleteFriend(id);
    }

    public List<User> getFriends(Integer id) {
        return inMemoryUserStorage.getUserById(id).getFriends().stream()
                .map(inMemoryUserStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        Set<Integer> otherIdFriends = inMemoryUserStorage.getUserById(otherId).getFriends();
        Set<Integer> userIdFriends = inMemoryUserStorage.getUserById(id).getFriends();
        if (otherIdFriends == null || userIdFriends == null) {
            return new ArrayList<User>();
        }
        List<User> friends = userIdFriends.stream()
                .filter(otherIdFriends::contains)
                .map(inMemoryUserStorage::getUserById)
                .collect(Collectors.toList());
        return friends;
    }
}
