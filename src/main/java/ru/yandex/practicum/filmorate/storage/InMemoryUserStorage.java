package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserExistingException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int idOfUser;

    @Override
    public User addUser(User user) {
        checkIfUserAdded(user);
        initializeFriendsPropertyIfNull(user);
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
        initializeFriendsPropertyIfNull(user);
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
            throw new UserExistingException(String.format("пользователя с id %d не существует.", id));
        }
    }

    private int generateId() {
        return ++idOfUser;
    }

    private boolean validateName(String name) {
        return name == null || name.isBlank();
    }

    private void checkIfUserAdded(User user) {
        Optional<User> savedUser = users.values().stream()
                .filter(element -> element.equals(user))
                .findFirst();
        if (savedUser.isPresent()) {
            log.error("такой пользователь уже добавлен");
            throw new UserNotFoundException("такой пользователь уже добавлен");
        }
    }

    private void initializeFriendsPropertyIfNull(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
    }
}


