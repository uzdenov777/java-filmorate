package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmLike;

@Repository
public interface FilmLikesRepository extends JpaRepository<FilmLike, Long> {

    @Modifying
    @Query("DELETE FROM FilmLike f " +
            "WHERE f.film.id = :filmId " +
            "AND f.user.id = :userId")
    void deleteLikeFilm(@Param("filmId") Long filmId, @Param("userId") Long userId);
}