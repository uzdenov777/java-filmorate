package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Genre getGenreById(int id) throws ResponseStatusException {
        boolean genreExist = genreDbStorage.isExistsGenre(id);
        if (!genreExist) {
            log.info("Не найден жанр при запросе на возврат по ID: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден жанр при запросе на возврат по ID: " + id);
        }

        return genreDbStorage.getGenreById(id);
    }

    public void isGenreExist(int id) throws ResponseStatusException {
        boolean genreExist = genreDbStorage.isExistsGenre(id);
        if (!genreExist) {
            log.info("Не найден жанр по ID: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден жанр по ID: " + id);
        }
    }

    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    public Set<Genre> getGenresByFilmId(Long filmId) {

        return new HashSet<>(genreDbStorage.getGenresByFilmId(filmId));
    }
}
