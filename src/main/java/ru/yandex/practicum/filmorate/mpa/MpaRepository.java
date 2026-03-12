package ru.yandex.practicum.filmorate.mpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MpaRepository extends JpaRepository<Mpa, Long> {
}