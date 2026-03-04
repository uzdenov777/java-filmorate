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
            LEFT JOIN film_likes fl ON f.id = fl.film_id
            GROUP BY f.id
            ORDER BY COUNT(fl.user_id) DESC, f.id ASC
            LIMIT :count
            """, nativeQuery = true)
    List<Film> getTopPopularFilms(@Param("count") int count);
}