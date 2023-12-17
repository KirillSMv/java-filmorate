package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.userDao.UserDao;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Repository("inMemoryUserDao")
public class InMemoryUserDao implements UserDao {
    private final Map<Integer, User> users = new HashMap<>();
    private int idOfUser;

    @Override
    public User addUser(User user) {
        checkIfUserAdded(user);
        user.setId(generateId());
        if (validateName(user.getName())) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkIfUserExists(user.getId());
        if (validateName(user.getName())) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        checkIfUserExists(id);
        return users.get(id);
    }

    @Override
    public void deleteUserById(Integer id) {
        checkIfUserExists(id);
        users.remove(id);
    }

    public void checkIfUserExists(Integer id) {
        if (!users.containsKey(id)) {
            log.error("пользователя с id {} не существует.", id);
            throw new UserNotFoundException(String.format("пользователя с id %d не существует.", id));
        }
    }

    private int generateId() {
        return ++idOfUser;
    }

    private boolean validateName(String name) {
        return name == null || name.isBlank();
    }

    @Override
    public void addToFriends(Integer id, Integer friendId) {
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
    }

    @Override
    public List<User> getFriends(Integer id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return null;
    }

    private void checkIfUserAdded(User user) {
        Optional<User> savedUser = users.values().stream()
                .filter(element -> element.equals(user))
                .findFirst();
        if (savedUser.isPresent()) {
            log.error("такой пользователь уже добавлен");
            throw new UserAlreadyExistsException("такой пользователь уже добавлен");
        }
    }

    public Map<Integer, User> getUsersMap() {
        return users;
    }
}

