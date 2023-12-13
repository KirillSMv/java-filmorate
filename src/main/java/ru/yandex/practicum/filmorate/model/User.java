package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    LocalDate birthday;

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}


/*


    public void deleteFriend(Integer id) {
        friends.remove(id);
    }

    public boolean checkIfFriends(Integer id) {
        return friends.contains(id);
    }
}*/