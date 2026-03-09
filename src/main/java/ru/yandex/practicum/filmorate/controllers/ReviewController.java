package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.dto.ReviewDto;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ReviewDto create(@RequestBody @Valid ReviewDto reviewDto) {
        log.info("Создание нового отзыва: {}", reviewDto);

        reviewDto.setUseful(0L);

        return reviewService.create(reviewDto);
    }

    @PutMapping
    public ReviewDto update(@RequestBody @Valid ReviewDto reviewDto) {
        log.info("Обновление отзыва: {}", reviewDto);

        return reviewService.update(reviewDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Удаление комментария по ID: {}", id);

        reviewService.deleteById(id);
    }

    @GetMapping("/{id}")
    public ReviewDto findById(@PathVariable Long id) {
        log.info("Запрос комментария по ID: {}", id);

        return reviewService.findById(id);
    }

    @GetMapping
    public Set<ReviewDto> getReviewsByFilm(@RequestParam(defaultValue = "0") Long filmId,
                                           @RequestParam(defaultValue = "10") Long count) {
        log.info("Возвращаем все отзывы фильма по ID: {}", filmId);

        return reviewService.getReviewsByFilm(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewDto addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавление лайка отзыву: {} пользователем: {}", id, userId);

        return reviewService.addGradeToReview(id, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewDto addDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавление дизлайка отзыву: {} пользователем: {}", id, userId);

        return reviewService.addGradeToReview(id, userId, false);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewDto deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удаление лайка отзыву: {} пользователем: {}", id, userId);

        return reviewService.deleteGradeToReview(id, userId, true);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewDto deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удаление дизлайка отзыву: {} пользователем: {}", id, userId);

        return reviewService.deleteGradeToReview(id, userId, false);
    }
}