package ru.yandex.practicum.filmorate.dao.filmDao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.filmDao.FilmDao;
import ru.yandex.practicum.filmorate.dao.filmDao.MpaDao;
import ru.yandex.practicum.filmorate.exceptions.FilmLikesException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository("FilmDaoImpl")
public class FilmDaoImpl implements FilmDao {
    private JdbcTemplate jdbcTemplate;
    private MpaDao mpaDao;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate, MpaDaoImpl mpaDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("id");
        int id = insert.executeAndReturnKey(filmToMap(film)).intValue();
        film.setId(id);
        saveGenres(film);
        if (film.getGenres() != null) {
            sortGenres(film);
        }
        return film;
    }

    @Override
    public Film getFilmById(Integer id) {
        if (checkIfFilmExists(id)) {
            Film film = jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE id = ?", getFilmMapper(), id);
            setGenreForFilm(film);
            return film;
        }
        log.error("фильм с id {} еще не добавлен.", id);
        throw new FilmNotFoundException(String.format("фильм с id %d еще не добавлен.", id));
    }

    @Override
    public Film updateFilm(Film film) {
        if (checkIfFilmExists(film.getId())) {
            Film savedFilm = getFilmById(film.getId());
            String sql = "UPDATE FILMS SET name = ?, release_date = ?, description = ?, duration = ?, mpa = ? WHERE id = ?";
            jdbcTemplate.update(sql, film.getName(), film.getReleaseDate(), film.getDescription(), film.getDuration(),
                    film.getMpa().getId(), film.getId());
            if (!savedFilm.getGenres().equals(film.getGenres())) {
                jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE film_id = ?", film.getId());
                saveGenres(film);
                if (film.getGenres() != null) {
                    sortGenres(film);
                }
            }
            return film;
        }
        log.error("фильм с id {} еще не добавлен.", film.getId());
        throw new FilmNotFoundException(String.format("фильм с id %d еще не добавлен.", film.getId()));
    }

    @Override
    public void deleteFilmById(Integer id) {
        if (checkIfFilmExists(id)) {
            jdbcTemplate.update("DELETE FROM FILMS WHERE id = ?", id);

        } else {
            log.error("фильм с id {} еще не добавлен.", id);
            throw new FilmNotFoundException(String.format("фильм с id %d еще не добавлен.", id));
        }
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        if (checkIfFilmExists(id) && checkIfUserExists(userId)) {
            try {
                jdbcTemplate.update("INSERT INTO USER_FILM(user_id, film_id) VALUES(?, ?);", userId, id);
            } catch (DataAccessException e) {
                log.error("Пользователь с id {} уже ставил лайк фильму с id {}", userId, id);
                throw new FilmLikesException(String.format("Пользователь с id %d уже ставил лайк фильму с id %d", userId, id));
            }
        } else {
            log.error("Неверно указаны параметры запроса {}, {}", id, userId);
            throw new FilmNotFoundException(String.format("Неверно указаны параметры запроса %d, %d", id, userId));
        }
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        if (checkIfFilmExists(id) && checkIfUserExists(userId)) {
            try {
                jdbcTemplate.update("DELETE FROM USER_FILM WHERE user_id = ? AND film_id = ?", userId, id);
            } catch (DataAccessException e) {
                log.error("Пользователь с id {} уже ставил лайк фильму с id {}", userId, id);
                throw new FilmLikesException(String.format("Пользователь с id %d уже ставил лайк фильму с id %d", userId, id));
            }
        } else {
            log.error("Неверно указаны данные для удаления лайка: userId {}, id {}", userId, id);
            throw new ObjectNotFoundException(String.format("Неверно указаны данные для удаления лайка: userId %d, id %d", userId, id));
        }
    }

    @Override
    public List<Film> getFilms() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM FILMS", getFilmMapper());
        setGenreForFilms(films);
        return films;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = jdbcTemplate.query("SELECT * " +
                "FROM FILMS AS f " +
                "LEFT OUTER JOIN USER_FILM AS uf " +
                "ON f.id = uf.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(uf.user_id) DESC " +
                "LIMIT ?", getFilmMapper(), count);
        setGenreForFilms(films);
        return films;
    }

    private void setGenreForFilms(List<Film> films) {
        Map<Integer, List<FilmGenre>> filmGenreMap = getFilmGenreMap(getFilmsId(films));
        for (Film film : films) {
            film.setGenres(getGenresForFilm(film.getId(), filmGenreMap));
        }
    }

    private Map<Integer, List<FilmGenre>> getFilmGenreMap(List<Integer> filmsId) {
        List<FilmGenre> genres = getGenres(filmsId);
        return genres.stream()
                .collect(Collectors.groupingBy(FilmGenre::getFilmId));
    }

    private List<Integer> getFilmsId(List<Film> films) {
        return films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
    }

    public RowMapper<Film> getFilmMapper() {
        List<Mpa> mpaList = getMpa();
        return (rs, rowNum) -> Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .mpa(mpaList.get(rs.getInt("mpa") - 1))
                .build();
    }

    private Set<Genre> getGenresForFilm(Integer filmId, Map<Integer, List<FilmGenre>> resultMapForFilm) {
        if (resultMapForFilm.get(filmId) == null) {
            log.error("Для фильма с id {} не указано жанров", filmId);
            return Collections.emptySet();
        }
        return resultMapForFilm.get(filmId).stream()
                .map(filmGenre -> new Genre(filmGenre.getGenreId(), filmGenre.getGenre()))
                .collect(Collectors.toSet());
    }

    private List<FilmGenre> getGenres(List<Integer> filmsId) {
        String filmsIdString = filmsId.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", " ", " "));
        return jdbcTemplate.query(
                "SELECT film_id, fg.genre_id, genre " +
                        "FROM FILM_GENRE AS fg " +
                        "JOIN GENRE AS g " +
                        "ON fg.genre_id = g.genre_id " +
                        "WHERE fg.film_id IN(" + filmsIdString + ")", getFilmGenreMapper());
    }

    private RowMapper<FilmGenre> getFilmGenreMapper() {
        return (rs, rowNum) -> new FilmGenre(
                rs.getInt("film_id"),
                rs.getInt("genre_id"),
                rs.getString("genre")
        );
    }

    public List<Mpa> getMpa() {
        return mpaDao.getMpa();
    }

    private Map<String, Object> filmToMap(Film film) {
        return Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "RELEASE_DATE", film.getReleaseDate(),
                "duration", film.getDuration(),
                "mpa", film.getMpa().getId()
        );
    }

    public void saveGenres(Film film) {
        if (film.getGenres() != null) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            jdbcTemplate.batchUpdate("INSERT INTO FILM_GENRE(film_id, genre_id) VALUES (?, ?);",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, film.getId());
                            ps.setInt(2, genres.get(i).getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return genres.size();
                        }
                    }
            );
        }
    }

    public void setGenreForFilm(Film film) {
        List<FilmGenre> filmGenreList = getGenres(Collections.singletonList(film.getId()));
        if (filmGenreList.isEmpty()) {
            log.error("Для фильма с id {} не указано жанров", film.getId());
            film.setGenres(Collections.emptySet());
        } else {
            film.setGenres(filmGenreList.stream()
                    .map(filmGenre -> new Genre(filmGenre.getGenreId(), filmGenre.getGenre()))
                    .collect(Collectors.toSet()));
            sortGenres(film);
        }
    }

    private void sortGenres(Film film) {
        HashSet<Genre> sortedHashSet = film.getGenres().stream()
                .sorted(this::compare)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        film.setGenres(sortedHashSet);
    }

    private int compare(Genre genre1, Genre genre2) {
        return Integer.compare(genre1.getId(), genre2.getId());
    }

    private boolean checkIfFilmExists(Integer id) {
        Integer result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM FILMS WHERE id = ?", Integer.class, id);
        if (result == 0) {
            log.error("фильм с id {} еще не добавлен.", id);
            return false;
        }
        return true;
    }

    private boolean checkIfUserExists(Integer id) {
        Integer result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USERS WHERE id = ?", Integer.class, id);
        if (result == 0) {
            log.error("Пользователя с таким id {} нет", id);
            return false;
        }
        return true;
    }
}
