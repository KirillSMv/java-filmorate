package ru.yandex.practicum.filmorate.dao.userDao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserFriendsDao {

    void addToFriends(Integer id, Integer friendId);

    void deleteFriend(Integer id, Integer friendId);


    List<User> getFriends(Integer id);


    List<User> getCommonFriends(Integer id, Integer otherId);

}
