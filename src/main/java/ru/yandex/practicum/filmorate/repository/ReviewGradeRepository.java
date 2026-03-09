package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewGrade;

@Repository
public interface ReviewGradeRepository extends JpaRepository<ReviewGrade, Long> {
}
