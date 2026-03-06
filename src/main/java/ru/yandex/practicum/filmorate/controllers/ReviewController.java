package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.dto.ReviewDto;
import ru.yandex.practicum.filmorate.service.ReviewService;

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

        ReviewDto ReviewDto = reviewService.create(reviewDto);
        return ReviewDto;
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


}

//GET /reviews?filmId={filmId}&count={count}
//Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано то 10.
//
//PUT /reviews/{id}/like/{userId} — пользователь ставит лайк отзыву.
//PUT /reviews/{id}/dislike/{userId} — пользователь ставит дизлайк отзыву.
//DELETE /reviews/{id}/like/{userId} — пользователь удаляет лайк/дизлайк отзыву.
//DELETE /reviews/{id}/dislike/{userId} — пользователь удаляет дизлайк отзыву.
//Описание JSON-объекта с которым работают эндпоинты
//{
//    "reviewId": 123,
//        "content": "This film is sooo baad.",
//        "isPositive": false,
//        "userId": 123, // Пользователь
//        "filmId": 2, // Фильм
//        "useful": 20 // рейтинг полезности
//}