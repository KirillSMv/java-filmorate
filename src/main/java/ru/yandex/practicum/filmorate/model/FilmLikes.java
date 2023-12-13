package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Data
@Component
public class FilmLikes {
    private int rate;
    private int film_id;

    public FilmLikes(int rate, int film_id) {
        this.rate = rate;
        this.film_id = film_id;
    }
}
