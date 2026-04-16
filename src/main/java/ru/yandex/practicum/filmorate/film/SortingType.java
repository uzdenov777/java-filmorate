package ru.yandex.practicum.filmorate.film;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum SortingType {
    YEAR,
    LIKES;

    public static SortingType fromString(String sortBy) {
        if (sortBy.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Передан пустой пустой SortingType");
        }

        try {
            return SortingType.valueOf(sortBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Неверный тип сортировки: " + sortBy + ". Допустимые значения: YEAR, LIKES"
            );
        }
    }
}