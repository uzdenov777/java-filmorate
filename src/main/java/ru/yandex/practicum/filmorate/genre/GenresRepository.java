package ru.yandex.practicum.filmorate.genre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.genre.model.Genre;

import java.util.Set;

@Repository
public interface GenresRepository extends JpaRepository<Genre, Long> {

    long countByIdIn(Set<Long> genreIds);
}