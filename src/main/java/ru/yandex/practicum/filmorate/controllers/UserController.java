package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        checkParameters(user);
        return userService.addUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) { //todo код 500 вместо 404
        checkParameters(user);
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping("/{id}")
    public String deleteUserById(@PathVariable("id") Integer id) {
        userService.deleteUserById(id);
        return String.format("Пользователь с id %d удален", id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public String addToFriends(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        userService.addToFriends(id, friendId);
        return "Пользователи добавлены в друзья";
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Integer id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer id, @PathVariable("otherId") Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public String deleteFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        userService.deleteFriend(id, friendId);
        return "Пользователи удалены из друзей";
    }

    private void checkParameters(User user) {
        if (checkIfNotSet(user.getEmail(), user.getLogin(), user.getBirthday())) {
            log.error("Заданы не все данные для пользователя {}, {}, {}", user.getEmail(),
                    user.getLogin(), user.getBirthday());
            throw new UserValidationException("Пожалуйста, убедитесь, что заданы все необходимые данные " +
                    "для пользователя: email, login, birthday");
        }
        if (user.getEmail().isBlank()) {
            String msg = "email не может быть пустым";
            log.error(msg);
            throw new UserValidationException(msg);
        }
        if (!user.getEmail().contains("@")) {
            log.error("email должен содердать символ @,введенный email: {}", user.getEmail());
            throw new UserValidationException("email должен содердать символ @");
        }
        if (user.getLogin().isBlank()) {
            String msg = "login не может быть пустым";
            log.error(msg);
            throw new UserValidationException(msg);
        }
        if (user.getLogin().contains(" ")) {
            log.error("login не может содержать пробелы, введенный login: {}", user.getLogin());
            throw new UserValidationException("login не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("День Рождения не может быть в будушем, введенная дата: {}", user.getBirthday());
            throw new UserValidationException("День Рождения не может быть в будушем");
        }
    }

    private boolean checkIfNotSet(String email, String login, LocalDate birthday) {
        return email == null || login == null || birthday == null;
    }
}
