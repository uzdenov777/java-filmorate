package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

@Repository
public interface FriendRepository extends JpaRepository<Friendship, Long> {

    @Modifying
    @Query("DELETE FROM Friendship f " +
            "WHERE f.user.id = :userId " +
            "AND f.friend.id = :friendId")
    void deleteByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("SELECT f.friend " +
            "FROM Friendship f " +
            "WHERE f.user.id = :userId")
    Page<User> findFriendsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.id IN " +
            "  ( SELECT f1.friend.id " +
            "    FROM Friendship f1 " +
            "    WHERE f1.user.id = :idUserFirst ) " +
            "AND u.id IN " +
            "  ( SELECT f2.friend.id " +
            "    FROM Friendship f2 " +
            "    WHERE f2.user.id = :idUserSecond ) ")
    Page<User> findMutualFriends(@Param("idUserFirst") Long idUserFirst,
                                 @Param("idUserSecond") Long idUserSecond,
                                 Pageable pageable);
}