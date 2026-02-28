package ru.yandex.practicum.filmorate.storage.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Optional;

@Repository
public interface MpaRepository extends JpaRepository<Mpa, Long> {
}