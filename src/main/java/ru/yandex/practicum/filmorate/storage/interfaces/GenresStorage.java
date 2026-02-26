package ru.yandex.practicum.filmorate.storage.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

@Repository
public interface GenresStorage extends JpaRepository<Genre, Long> {
}