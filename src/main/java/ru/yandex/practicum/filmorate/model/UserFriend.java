package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class UserFriend {
    private int userId;
    private int friendId;

    public UserFriend(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }
}
