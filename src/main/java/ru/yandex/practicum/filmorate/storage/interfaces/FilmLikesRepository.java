package ru.yandex.practicum.filmorate.storage.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;

import java.util.List;

@Repository
public interface FilmLikesRepository extends JpaRepository<FilmLike, Long> {

    @Modifying
    @Query("DELETE FROM FilmLike f " +
            "WHERE f.film.id = :filmId " +
            "AND f.user.id = :userId")
    void deleteLikeFilm(long filmId, long userId);

    @Query("SELECT fl.film " +
            "FROM FilmLike fl " +
            "GROUP BY fl.film " +
            "ORDER BY COUNT(fl.user) DESC " +
            "LIMIT :count")
    List<Film> getTopPopularFilms(@Param("count") int count);
}