package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorRepository extends JpaRepository<Director, Long> {
}
