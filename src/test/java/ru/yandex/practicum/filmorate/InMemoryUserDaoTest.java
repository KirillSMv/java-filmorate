package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserDao;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryUserDaoTest {
    private InMemoryUserDao inMemoryUserDao;

    @BeforeEach
    public void setup() {
        inMemoryUserDao = new InMemoryUserDao();
    }

    @Test
    public void testAddUserWhenDuplication() {
        User user = User.builder()
                .login("login")
                .birthday(LocalDate.of(2020, 10, 10))
                .email("email@email")
                .build();
        inMemoryUserDao.getUsersMap().put(1, user);
        assertThrows(UserAlreadyExistsException.class, () -> inMemoryUserDao.addUser(user));
    }

    @Test
    public void givenCorrectParameters_whenAddUser_thenShouldTrowException() {
        User user = User.builder()
                .name("name")
                .login("login")
                .birthday(LocalDate.of(2020, 10, 10))
                .email("email@email")
                .build();
        assertEquals(user, inMemoryUserDao.addUser(user));
    }
}
