package ru.yandex.practicum.filmorate.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = """
            SELECT fl.user_id, COUNT(*) AS common_likes
            FROM film_likes fl
            WHERE fl.film_id IN (
                SELECT film_id
                FROM film_likes
                WHERE user_id = :userId
            )
            AND fl.user_id <> :userId
            GROUP BY fl.user_id
            ORDER BY common_likes DESC
            LIMIT 1
            """, nativeQuery = true)
    Long findSimilarUserByUserId(@Param("userId") long id);
}