package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.UserParametersException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerValidationTest {
    UserController controller = new UserController();


    @Test
    public void givenEmptyEmail_whenCheckParameters_thenValidationFailed() {
        User user = User.builder()
                .name("name")
                .login("login@login")
                .birthday(LocalDate.of(2021, 10, 10))
                .email(" ")
                .build();
        boolean areParametersInCorrect = controller.checkParameters(user.getEmail(), user.getLogin(), user.getBirthday());
        assertEquals(true, areParametersInCorrect);
    }

    @Test
    public void givenIncorrectEmail_whenCheckParameters_thenValidationFailed() {
        User user = User.builder()
                .name("name")
                .login("login@login")
                .birthday(LocalDate.of(2021, 10, 10))
                .email("email")
                .build();
        boolean areParametersInCorrect = controller.checkParameters(user.getEmail(), user.getLogin(), user.getBirthday());
        assertEquals(true, areParametersInCorrect);
    }

    @Test
    public void givenBlankLogin_whenCheckParameters_thenValidationFailed() {
        User user = User.builder()
                .name("name")
                .login(" ")
                .birthday(LocalDate.of(2021, 10, 10))
                .email("email@email")
                .build();
        boolean areParametersInCorrect = controller.checkParameters(user.getEmail(), user.getLogin(), user.getBirthday());
        assertEquals(true, areParametersInCorrect);
    }

    @Test
    public void givenIncorrectLogin_whenCheckParameters_thenValidationFailed() {
        User user = User.builder()
                .name("name")
                .login("login ")
                .birthday(LocalDate.of(2021, 10, 10))
                .email("email@email")
                .build();
        boolean areParametersInCorrect = controller.checkParameters(user.getEmail(), user.getLogin(), user.getBirthday());
        assertEquals(true, areParametersInCorrect);
    }

    @Test
    public void givenIncorrectBirthday_whenCheckParameters_thenValidationFailed() {
        User user = User.builder()
                .name("name")
                .login("login")
                .birthday(LocalDate.of(2025, 10, 10))
                .email("email@email")
                .build();
        boolean areParametersInCorrect = controller.checkParameters(user.getEmail(), user.getLogin(), user.getBirthday());
        assertEquals(true, areParametersInCorrect);
    }

    @Test
    public void givenEmptyLogin_whenCheckParameters_thenShouldTrowException() {
        User user = User.builder()
                .name("name")
                .birthday(LocalDate.of(2020, 10, 10))
                .email("email@email")
                .build();
        final UserParametersException exception = assertThrows(
                UserParametersException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        boolean areParametersInCorrect = controller.checkParameters(user.getEmail(), user.getLogin(), user.getBirthday());
                    }
                });
        assertEquals("Пожалуйста, убедитесь, что заданы все необходимые данные для пользователя: email, login, birthday", exception.getMessage());
    }

    @Test
    public void givenEmptyBirthday_whenCheckParameters_thenShouldTrowException() {
        User user = User.builder()
                .name("name")
                .login("login")
                .email("email@email")
                .build();
        final UserParametersException exception = assertThrows(
                UserParametersException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        boolean areParametersInCorrect = controller.checkParameters(user.getEmail(), user.getLogin(), user.getBirthday());
                    }
                });
        assertEquals("Пожалуйста, убедитесь, что заданы все необходимые данные для пользователя: email, login, birthday", exception.getMessage());
    }

    @Test
    public void givenEmptyEmail_whenCheckParameters_thenShouldTrowException() {
        User user = User.builder()
                .name("name")
                .login("login")
                .birthday(LocalDate.of(2020, 10, 10))
                .build();
        final UserParametersException exception = assertThrows(
                UserParametersException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        boolean areParametersInCorrect = controller.checkParameters(user.getEmail(), user.getLogin(), user.getBirthday());
                    }
                });
        assertEquals("Пожалуйста, убедитесь, что заданы все необходимые данные для пользователя: email, login, birthday", exception.getMessage());
    }

    @Test
    public void givenEmptyName_whenCheckParameters_thenShouldTrowException() {
        User user = User.builder()
                .login("login")
                .birthday(LocalDate.of(2020, 10, 10))
                .email("email@email")
                .build();
        boolean areParametersInCorrect = controller.checkParameters(user.getEmail(), user.getLogin(), user.getBirthday());
        assertEquals(false, areParametersInCorrect);
    }

    @Test
    public void givenCorrectParameters_whenCheckParameters_thenShouldTrowException() {
        User user = User.builder()
                .name("name")
                .login("login")
                .birthday(LocalDate.of(2020, 10, 10))
                .email("email@email")
                .build();
        boolean areParametersInCorrect = controller.checkParameters(user.getEmail(), user.getLogin(), user.getBirthday());
        assertEquals(false, areParametersInCorrect);
    }
}
