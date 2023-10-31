package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotAddedException;
import ru.yandex.practicum.filmorate.exceptions.UserParametersException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    Map<Integer, User> users = new HashMap<>();
    private int idOfUser;

    @PostMapping("/users")
    public User addUser(@RequestBody User user) throws UserAlreadyExistsException, UserValidationException, UserParametersException {
        boolean areParametersIncorrect = checkParameters(user.getEmail(), user.getLogin(), user.getBirthday());

        if (areParametersIncorrect) {
            log.debug("Некорректно указаны данные для пользователя {}, {}, {}", user.getEmail(), user.getLogin(), user.getBirthday());
            throw new UserValidationException("Неверное указаны параметры.", user.getEmail(), user.getLogin(), user.getBirthday());
        }
        user.setId(generateId());
        if (users.containsKey(user.getId())) {
            log.debug("пользователь с id {} уже существует.", user.getId());
            throw new UserAlreadyExistsException("Пользователь с id " + user.getId() + " уже существует.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("Сохраняемый объект: {}", user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) throws UserValidationException, UserParametersException, UserNotAddedException {
        boolean areParametersInCorrect = checkParameters(user.getEmail(), user.getLogin(), user.getBirthday());

        if (areParametersInCorrect) {
            log.debug("Некорректно указаны данные для пользователя {}, {}, {}", user.getEmail(), user.getLogin(), user.getBirthday());
            throw new UserValidationException("Неверное указаны параметры.", user.getEmail(), user.getLogin(), user.getBirthday());
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (!users.containsKey(user.getId())) {
            throw new UserNotAddedException("Такого пользователя нет, пожалуйста, в начале добавьте пользователя с id: " + user.getId());
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

    public boolean checkParameters(String email, String login, LocalDate birthday) throws UserParametersException {
        if (checkIfNotSet(email, login, birthday)) {
            log.debug("Заданы не все данные для пользователя {}, {}, {}", email, login, birthday);
            throw new UserParametersException("Пожалуйста, убедитесь, что заданы все необходимые данные для пользователя: email, login, birthday");
        }
        return email.isBlank() || !email.contains("@") || login.isBlank() || login.contains(" ") || birthday.isAfter(LocalDate.now());
    }

    private int generateId() {
        return ++idOfUser;
    }

    private boolean checkIfNotSet(String email, String login, LocalDate birthday) {
        return email == null || login == null || birthday == null;
    }
}
