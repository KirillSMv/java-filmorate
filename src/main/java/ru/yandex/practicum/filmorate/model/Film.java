package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Integer> likes; //Users id

    public boolean addLike(Integer userId) {
        return likes.add(userId);
    }

    public boolean removeLike(Integer userId) {
        return likes.remove(userId);
    }
}


