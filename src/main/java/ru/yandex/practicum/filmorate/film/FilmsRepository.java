package ru.yandex.practicum.filmorate.film;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.film.model.Film;

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
    List<Film> getTopPopularFilms(@Param("count") int count,
                                  @Param("genreId") Long genreId,
                                  @Param("year") Long year);

    @Query("""
            SELECT f
            FROM Film f
            LEFT JOIN FilmLike fl ON fl.film = f
            WHERE f.id IN (
                 SELECT fl1.film.id
                 FROM FilmLike fl1
                 WHERE fl1.user.id = :userId
            )
            AND f.id IN (
                 SELECT fl2.film.id
                 FROM FilmLike fl2
                 WHERE fl2.user.id = :friendId
            )
            GROUP BY f
            ORDER BY COUNT(fl.user) DESC, f.id ASC
            """)
    Page<Film> findCommonLikedFilms(Long userId,
                                    Long friendId,
                                    Pageable pageable);

    @Query(value = """
                 SELECT f.*
                 FROM films f
                 JOIN film_directors fd ON f.id = fd.film_id
                 WHERE fd.director_id = :directorId
                 ORDER BY f.release_date ASC
            """, nativeQuery = true)
    Page<Film> findByDirectorsIdOrderByReleaseDate(@Param("directorId") Long directorId,
                                                   Pageable pageable);

    @Query(value = """
            SELECT f.*
            FROM films f
            JOIN film_directors fd ON f.id = fd.film_id
            LEFT JOIN (
                SELECT fl.film_id, COUNT(*) AS likes_count
                FROM film_likes fl
                GROUP BY fl.film_id
            ) lc ON lc.film_id = f.id
            WHERE fd.director_id = :directorId
            ORDER BY COALESCE(lc.likes_count, 0) DESC, f.id ASC
            """, nativeQuery = true)
    Page<Film> findByDirectorsIdOrderByLikes(@Param("directorId") Long directorId,
                                             Pageable pageable);

    @Query(value = """
            SELECT f.*
            FROM films f
            JOIN film_likes fl ON f.id = fl.film_id
            WHERE fl.user_id = :similarUserId
            AND f.id NOT IN (
                SELECT film_id
                FROM film_likes
                WHERE user_id = :userId
            )
            """, nativeQuery = true)
    List<Film> findRecommendations(@Param("userId") long id,
                                   @Param("similarUserId") long similarUserId);

    @Query(value = """
            SELECT f.*
            FROM films f
            LEFT JOIN film_directors fd ON f.id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.id
            WHERE ((:byIsTitle = true AND f.name ILIKE CONCAT('%', :query, '%'))
                  OR (:byIsDirector = true AND d.name ILIKE CONCAT('%', :query, '%')))
            GROUP BY f.id
            """, nativeQuery = true)
    List<Film> searchFilms(@Param("query") String query,
                           @Param("byIsTitle") boolean byIsTitle,
                           @Param("byIsDirector") boolean byIsDirector);
}