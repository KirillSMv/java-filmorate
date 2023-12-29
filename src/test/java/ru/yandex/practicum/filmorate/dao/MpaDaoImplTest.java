package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.filmDao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class MpaDaoImplTest {
    private MpaDaoImpl mpaDaoImpl;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        mpaDaoImpl = new MpaDaoImpl(jdbcTemplate);
    }

    @Test
    public void testGetMpa() {
        List<Mpa> mpaList = new ArrayList<>();
        mpaList.add(new Mpa(1, "G"));
        mpaList.add(new Mpa(2, "PG"));
        mpaList.add(new Mpa(3, "PG-13"));
        mpaList.add(new Mpa(4, "R"));
        mpaList.add(new Mpa(5, "NC-17"));

        List<Mpa> savedMpaList = mpaDaoImpl.getMpa();
        assertEquals(mpaList, savedMpaList);
    }

    @Test
    public void testGetGenreById() {
        Mpa mpa1 = new Mpa(1, "G");
        Mpa savedMpa = mpaDaoImpl.getMpaById(1);

        assertEquals(mpa1, savedMpa);

    }
}
