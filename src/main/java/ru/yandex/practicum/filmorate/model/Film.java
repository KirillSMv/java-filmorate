package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.time.LocalDate;

@Builder
@Data
public class Film {
    private int id;
    private final String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
}
