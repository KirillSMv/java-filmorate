# java-filmorate
Template repository for Filmorate project.
![Filmorate_database](https://github.com/KirillSMv/java-filmorate/assets/88364531/41947d29-721f-4fd6-99e5-adfb3c1acf89)


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
