package ru.yandex.practicum.filmorate.storage.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

@Repository
public interface GenresRepository extends JpaRepository<Genre, Long> {

    @Query("SELECT g FROM Genre g WHERE g.id IN :ids")
    List<Genre> findAllByIdInBatch(@Param("ids") List<Long> ids);
}