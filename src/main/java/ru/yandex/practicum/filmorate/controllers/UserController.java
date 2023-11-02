package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserExistingException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idOfUser;

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        checkParameters(user);
        user.setId(generateId());
        if (validateName(user.getName())) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("Сохраняемый объект: {}", user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        checkParameters(user);
        if (validateName(user.getName())) {
            user.setName(user.getLogin());
        }
        if (!users.containsKey(user.getId())) {
            log.warn("пользователя с id {} еще не существует.", user.getId());
            throw new UserExistingException("Такого пользователя нет, пожалуйста, в начале добавьте пользователя с id: " + user.getId());
        }
        users.put(user.getId(), user);
        log.debug("Сохраняемый объект: {}", user);
        return user;
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        log.debug("Количество пользователей на текущий момент: {}", users.size());
        return users.values();
    }

    private void checkParameters(User user) {
        if (checkIfNotSet(user.getEmail(), user.getLogin(), user.getBirthday())) {
            log.error("Заданы не все данные для пользователя {}, {}, {}", user.getEmail(), user.getLogin(), user.getBirthday());
            throw new UserValidationException("Пожалуйста, убедитесь, что заданы все необходимые данные для пользователя: email, login, birthday");
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
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("День Рождения не может быть в будушем, введенная дата: {}", user.getBirthday());
            throw new UserValidationException("День Рождения не может быть в будушем");
        }
    }

    private int generateId() {
        return ++idOfUser;
    }

    private boolean checkIfNotSet(String email, String login, LocalDate birthday) {
        return email == null || login == null || birthday == null;
    }

    private boolean validateName(String name) {
        return name == null || name.isBlank();
    }
}
