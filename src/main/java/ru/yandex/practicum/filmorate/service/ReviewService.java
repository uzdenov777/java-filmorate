package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewGrade;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.ReviewDto;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;

import java.util.List;
import java.util.Set;

@Service
public class ReviewService {

    private final UserService userService;
    private final FilmService filmService;
    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;

    public ReviewService(UserService userService, FilmService filmService, ReviewMapper reviewMapper, ReviewRepository reviewRepository) {
        this.userService = userService;
        this.filmService = filmService;
        this.reviewMapper = reviewMapper;
        this.reviewRepository = reviewRepository;
    }

    public ReviewDto create(ReviewDto reviewDto) {
        existsFilmAndUser(reviewDto);

        Review review = reviewMapper.toEntity(reviewDto);
        Review saved = reviewRepository.save(review);

        return reviewMapper.toDto(saved);
    }

    public ReviewDto update(@Valid ReviewDto reviewDto) {
        existsFilmAndUser(reviewDto);

        Review review = reviewMapper.toEntity(reviewDto);
        Review saved = reviewRepository.save(review);

        return reviewMapper.toDto(saved);
    }

    public void deleteById(Long id) {
        var isExistsReview = reviewRepository.existsById(id);

        if (!isExistsReview) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден отзыв для удаления по ID: " + id);
        }

        reviewRepository.deleteById(id);
    }


    public ReviewDto findById(Long id) {
        return reviewRepository.findById(id)
                .map(reviewMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Не найден отзыв при запросе на возврат по ID: " + id));
    }

    public Set<ReviewDto> getReviewsByFilm(Long filmId, Long count) {
        var isDefault = filmId == 0;
        if (isDefault) {
            return getAllReviews(count);
        }

        var isExistFilm = filmService.isFilmExists(filmId);
        if (!isExistFilm) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден фильм для возвращения его отзывов");
        }

        List<Review> reviews = reviewRepository.findByFilmId(filmId, count);
        return reviewMapper.toDtos(reviews);
    }

    public ReviewDto addGradeToReview(Long reviewId, Long userId, Boolean isPositive) {
        User user = userService.getById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Не найден пользователь: " + userId + " для оценки отзыва: " + reviewId));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Не найден отзыв: " + reviewId + " для оценки пользователя: " + userId));

        ReviewGrade grade = new ReviewGrade();
        grade.setUser(user);
        grade.setIsPositive(isPositive);

        removeOppositeReviewGrade(review, user, isPositive);

        review.addGrade(grade);

        Review saved = reviewRepository.save(review);
        return reviewMapper.toDto(saved);
    }

    public ReviewDto deleteGradeToReview(Long reviewId, Long userId, Boolean isPositive) {
        User user = userService.getById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Не найден пользователь: " + userId + " для оценки отзыва: " + reviewId));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Не найден отзыв: " + reviewId + " для оценки пользователем: " + userId));

        ReviewGrade grade = review.getGradeByUserAndPositiveType(user, isPositive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "По параметрам reviewId: " + review.getId()
                                + ", userId: " + user.getId()
                                + ", isPositive: " + isPositive
                                + " не найдена оценка отзыва для удаления"));

        review.removeGrade(grade);

        Review saved = reviewRepository.save(review);
        return reviewMapper.toDto(saved);
    }

    private void removeOppositeReviewGrade(Review review, User user, boolean isPositive) {
        review.getGradeByUserAndPositiveType(user, !isPositive)
                .ifPresent(review::removeGrade);
    }

    private Set<ReviewDto> getAllReviews(Long count) {
        List<Review> reviews = reviewRepository.findAll(count);

        return reviewMapper.toDtos(reviews);
    }

    private void existsFilmAndUser(ReviewDto reviewDto) {
        var isExistUser = userService.isUserExists(reviewDto.getUserId());
        var isExistFilm = filmService.isFilmExists(reviewDto.getFilmId());

        if (!(isExistUser && isExistFilm)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден фильм или пользователь указанные в отзыва");
        }
    }
}