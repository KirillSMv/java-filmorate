package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerValidationTest { //проверка валидации
    private UserController controller;
    @BeforeEach
    public void setup() {
        controller = new UserController();
    }


    @Test
    public void givenEmptyEmail_whenAddUser_thenValidationFailed() {
        User user = User.builder()
                .name("name")
                .login("login@login")
                .birthday(LocalDate.of(2021, 10, 10))
                .email(" ")
                .build();
        assertThrows(UserValidationException.class, () -> controller.addUser(user));
    }

    @Test
    public void givenIncorrectEmail_whenAddUser_thenValidationFailed() {
        User user = User.builder()
                .name("name")
                .login("login@login")
                .birthday(LocalDate.of(2021, 10, 10))
                .email("email")
                .build();
        assertThrows(UserValidationException.class, () -> controller.addUser(user));
    }

    @Test
    public void givenBlankLogin_whenAddUser_thenValidationFailed() {
        User user = User.builder()
                .name("name")
                .login(" ")
                .birthday(LocalDate.of(2021, 10, 10))
                .email("email@email")
                .build();
        assertThrows(UserValidationException.class, () -> controller.addUser(user));
    }

    @Test
    public void givenIncorrectLogin_whenAddUser_thenValidationFailed() {
        User user = User.builder()
                .name("name")
                .login("login ")
                .birthday(LocalDate.of(2021, 10, 10))
                .email("email@email")
                .build();
        assertThrows(UserValidationException.class, () -> controller.addUser(user));
    }

    @Test
    public void givenIncorrectBirthday_whenAddUser_thenValidationFailed() {
        User user = User.builder()
                .name("name")
                .login("login")
                .birthday(LocalDate.of(2025, 10, 10))
                .email("email@email")
                .build();
        assertThrows(UserValidationException.class, () -> controller.addUser(user));
    }

    @Test
    public void givenEmptyLogin_whenAddUser_thenShouldTrowException() {
        User user = User.builder()
                .name("name")
                .birthday(LocalDate.of(2020, 10, 10))
                .email("email@email")
                .build();
        assertThrows(UserValidationException.class, () -> controller.addUser(user));
    }

    @Test
    public void givenEmptyBirthday_whenAddUser_thenShouldTrowException() {
        User user = User.builder()
                .name("name")
                .login("login")
                .email("email@email")
                .build();
        assertThrows(UserValidationException.class, () -> controller.addUser(user));
    }

    @Test
    public void givenEmptyEmail_whenAddUser_thenShouldTrowException() {
        User user = User.builder()
                .name("name")
                .login("login")
                .birthday(LocalDate.of(2020, 10, 10))
                .build();
        assertThrows(UserValidationException.class, () -> controller.addUser(user));
    }

    @Test
    public void givenEmptyName_whenAddUser_thenShouldTrowException() {
        User user = User.builder()
                .login("login")
                .birthday(LocalDate.of(2020, 10, 10))
                .email("email@email")
                .build();
        assertEquals(user, controller.addUser(user));
    }

    @Test
    public void givenCorrectParameters_whenAddUser_thenShouldTrowException() {
        User user = User.builder()
                .name("name")
                .login("login")
                .birthday(LocalDate.of(2020, 10, 10))
                .email("email@email")
                .build();
        assertEquals(user, controller.addUser(user));
    }
}
