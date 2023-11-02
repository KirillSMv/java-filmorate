package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Builder
@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    LocalDate birthday;
}
