package ru.yandex.practicum.filmorate.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReviewDto {

    @JsonProperty("reviewId")
    @Positive(message = "ID отзыва не может быть отрицательным")
    private Long id;

    @NotBlank(message = "Пустой комментарий нельзя оставить")
    private String content;

    @JsonProperty("isPositive")
    @NotNull(message = "Комментарий должен быть позитивный или негативный")
    private Boolean positive;

    @NotNull(message = "Комментарий должен иметь пользователя")
    private Long userId;

    @NotNull(message = "Комментарий должен иметь фильм")
    private Long filmId;

    @JsonSetter(nulls = Nulls.SKIP) // Игнорировать null из JSON
    private Long useful = 0L;
}