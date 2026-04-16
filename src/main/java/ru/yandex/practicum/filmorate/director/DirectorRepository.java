package ru.yandex.practicum.filmorate.director;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.filmorate.director.model.Director;

import java.util.Set;

public interface DirectorRepository extends JpaRepository<Director, Long> {

    long countByIdIn(Set<Long> directorsId);
}