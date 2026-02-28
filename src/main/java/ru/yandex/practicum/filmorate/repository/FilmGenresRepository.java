package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

@Repository
public interface FilmGenresRepository extends JpaRepository<FilmGenre, Long> {

    @Modifying
    @Query("DELETE FROM FilmGenre f " +
            "WHERE f.film = :film")
    void deleteAllFilmGenresByFilm(Film film);

    @Query("SELECT fg.genre FROM FilmGenre fg WHERE fg.film = :film")
    Set<Genre> getGenresByFilm(@Param("film") Film film);
}