package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    //добавление, удаление, модификация объектов
    //получение объекта? todo
    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(String name);

    Film getFilm(String name);
}
