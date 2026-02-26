//package ru.yandex.practicum.filmorate.storage.interfaces;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import ru.yandex.practicum.filmorate.model.FilmGenres;
//import ru.yandex.practicum.filmorate.model.Genre;
//
//import java.util.List;
//
//@Repository
//public interface FilmGenresStorage extends JpaRepository<FilmGenres, Long> {
//
//    void deleteFilmGenresByFilmId(Long filmId);
//
//    List<Genre> getGenresByFilmId(Long filmId);
//}