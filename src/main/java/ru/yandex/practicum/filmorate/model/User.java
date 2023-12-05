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

/*
select f.name from film as f
join film_genre as fg on f.id = fg.film_id
join genre as g on fg.genre_id  = g.genre_id
where g.genre = 'Комедия';



insert into public.user (user_id, email, login, "name", birthday) values (1, 'K2@gmail.com', 'логин1', 'Илья', '2000-05-10');
insert into public.user (user_id, email, login, "name", birthday) values (2, 'email@gmail.com', 'логин2', 'Женя', '2015-05-10');
insert into public.user (user_id, email, login, "name", birthday) values (3, 'patry@gmail.com', 'логин3', 'Коля', '1995-05-10');
insert into public.user (user_id, email, login, "name", birthday) values (4, 'work@gmail.com', 'логин4', 'Миша', '1980-05-10');
insert into public.user (user_id, email, login, "name", birthday) values (5, 'home@gmail.com', 'логин5', 'Илья', '1988-05-10');

insert into public.user_friend (user_id, friend_id, status) values (1, 2, 'true');
INSERT INTO public.user_friend (user_id, friend_id, status) values (2, 3, 'false');
insert into public.user_friend (user_id, friend_id, status) values (2, 1, 'true');
insert into public.user_friend (user_id, friend_id, status) values (3, 4, 'true');
insert into public.user_friend (user_id, friend_id, status) values (4, 1, 'false');
insert into public.user_friend (user_id, friend_id, status) values (5, 5, 'true');


--запросы для таблицы с фильмами
--найти фильм с рейтингом 'PG'
SELECT name FROM film WHERE rating = 'PG';

--получить 2 наименее популярных фильма
SELECT f.name FROM film AS f
JOIN film_user as fu on f.id = fu.film_id
GROUP BY f.name
ORDER BY count(fu.user_id)
LIMIT 2;

--найти фильм выпущенный после '1990-10-10'
SELECT name FROM film WHERE film.release_date > '1990-10-10';

--найти имена пользователей, которым понравился фильм 'Форрест Гамп'
SELECT name FROM users WHERE user_id IN (
SELECT user_id FROM film_user fu JOIN film f ON fu.film_id = f.id
WHERE f.name = 'Форрест Гамп'
);

--запоросы для таблицы с пользователями
--пользователь с днем рождения после '2000-10-10'
SELECT name FROM users WHERE birthday > '2000-10-10';

--все друзья пользователя с имененм 'Коля'
SELECT name FROM users WHERE user_id IN (SELECT uf.friend_id FROM user_friend uf JOIN users AS u ON u.user_id = uf.user_id
WHERE uf.status = 'true' AND u.name = 'Коля');

--пользователь с наибольшим количеством друзей
SELECT name FROM users AS u JOIN user_friend AS uf ON u.user_id = uf.user_id
WHERE uf.status = 'true'
GROUP BY name
ORDER BY COUNT(friend_id) DESC
LIMIT 1;

--список общих друзей пользователя 'Миша' и пользователя 'Женя'
SELECT name FROM users WHERE user_id IN (
SELECT friend_id FROM user_friend uf JOIN users AS u ON u.user_id = uf.user_id
WHERE u.name = 'Женя'
AND friend_id IN (
SELECT friend_id FROM user_friend uf JOIN users AS u ON u.user_id = uf.user_id
WHERE u.name = 'Миша'));






 */
