package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    LocalDate birthday;
    Set<Integer> friends;

    public boolean addToFriends(Integer id) {
        return friends.add(id);
    }

    public void deleteFriend(Integer id) {
        friends.remove(id);
    }

    public boolean checkIfFriends(Integer id) {
        return friends.contains(id);
    }
}
