package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.repository.ReviewGradeRepository;

@Service
public class ReviewGradeService {

    private final ReviewGradeRepository reviewGradeRepository;

    public ReviewGradeService(ReviewGradeRepository reviewGradeRepository) {
        this.reviewGradeRepository = reviewGradeRepository;
    }


}