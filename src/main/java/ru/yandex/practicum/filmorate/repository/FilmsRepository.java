package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
public interface FilmsRepository extends JpaRepository<Film, Long> {

    @Query(value = """
            SELECT f.*
            FROM films f
            LEFT JOIN (
                SELECT film_id, COUNT(*) AS likes_count
                FROM film_likes
                GROUP BY film_id
            ) lc ON lc.film_id = f.id
            WHERE (:genreId = 0 OR EXISTS (
                    SELECT 1 FROM film_genres fg
                    WHERE fg.film_id = f.id AND fg.genre_id = :genreId
                ))
              AND (:year = 0 OR EXTRACT(YEAR FROM f.release_date) = :year)
            ORDER BY COALESCE(lc.likes_count, 0) DESC, f.id ASC
            LIMIT :count
            """, nativeQuery = true)
    List<Film> getTopPopularFilms(@Param("count") int count, @Param("genreId") Long genreId, @Param("year") Long year);
}