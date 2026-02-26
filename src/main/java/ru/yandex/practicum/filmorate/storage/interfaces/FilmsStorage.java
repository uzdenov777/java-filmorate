package ru.yandex.practicum.filmorate.storage.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

@Repository
public interface FilmsStorage extends JpaRepository<Film, Long> {
}