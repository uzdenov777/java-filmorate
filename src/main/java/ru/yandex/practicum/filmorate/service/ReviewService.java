package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.dto.ReviewDto;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;

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

    private void existsFilmAndUser(ReviewDto reviewDto) {
        var isExistUser = userService.isUserExists(reviewDto.getUserId());
        var isExistFilm = filmService.isFilmExists(reviewDto.getFilmId());

        if (!(isExistUser && isExistFilm)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден фильм или пользователь указанные в отзыва");
        }
    }
}
