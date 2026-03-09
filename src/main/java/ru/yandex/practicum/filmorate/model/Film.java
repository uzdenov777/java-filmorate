package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 * Название не может быть пустым;
 * максимальная длина описания — 200 символов;
 * дата релиза — не раньше 28 декабря 1895 года;
 * продолжительность фильма должна быть положительной.
 */


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "films")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Название у фильмы не может отсутствовать")
    private String name;

    @Column(name = "description")
    @NotBlank(message = "Описание у фильма не должно отсутствовать")
    @Size(max = 200, message = "Описание у фильма не должно превышать 200 символов")
    private String description;

    @Column(name = "release_date")
    @NotNull(message = "Дата релиза у фильмы не может отсутствовать")
    private LocalDate releaseDate;

    @Column(name = "duration")
    @NotNull(message = "Продолжительность у фильмы не может отсутствовать")
    @Min(value = 1, message = "Продолжительность у фильма не должна быть меньше 1")
    private Long duration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mpa_id")
    private Mpa mpa;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "FILM_GENRES",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @OrderBy("id ASC") // сортировка по id
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "FILM_DIRECTORS",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    private Set<Director> directors = new HashSet<>();
}