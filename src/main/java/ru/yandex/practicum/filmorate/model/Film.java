package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class Film {
    private Integer id;
    private String name;
    private LocalDate releaseDate;
    private String description;
    private int duration;
    private Mpa mpa;  //рейтинг
    private Set<Genre> genres;

    public Film() {
    }

    ;

    public Film(Integer id, String name, LocalDate releaseDate, String description, int duration, Mpa mpa, Set<Genre> genres) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.description = description;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

}
