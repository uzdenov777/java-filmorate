package ru.yandex.practicum.filmorate.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.review.model.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value = """
            SELECT *
            FROM reviews r
            ORDER BY r.id DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Review> findAll(@Param("limit") Long limit);

    @Query(value = """
            SELECT *
            FROM reviews r
            WHERE r.film_id = :filmId
            ORDER BY r.id DESC
            LIMIT :count
            """, nativeQuery = true)
    List<Review> findByFilmId(@Param("filmId") Long filmId, @Param("count") Long count);
}