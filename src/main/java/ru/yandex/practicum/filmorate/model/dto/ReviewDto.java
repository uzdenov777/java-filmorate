package ru.yandex.practicum.filmorate.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReviewDto {

    @Positive(message = "ID отзыва не может быть отрицательным")
    private Long id;

    @NotBlank(message = "Пустой комментарий нельзя оставить")
    private String content;

    @NotNull(message = "Комментарий должен быть позитивный или негативный")
    private Boolean isPositive;

    @NotNull(message = "Комментарий должен иметь пользователя")
    private Long userId;

    @NotNull(message = "Комментарий должен иметь фильм")
    private Long filmId;

    private Long useful = 0L;
}
