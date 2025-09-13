package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenresDbStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GenresService {
    private final GenresDbStorage genresDbStorage;

    public GenresService(GenresDbStorage genresDbStorage) {
        this.genresDbStorage = genresDbStorage;
    }

    public Genre getGenreById(int genreId) throws ResponseStatusException {
        Optional<Genre> genreOpt = genresDbStorage.findById(genreId);
        if (genreOpt.isEmpty()) {
            log.info("Не найден жанр при запросе на возврат по ID: {}", genreId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден жанр при запросе на возврат по ID: " + genreId);
        }

        Genre genre = genreOpt.get();
        return genre;
    }

    public List<Genre> getAllGenres() {
        return genresDbStorage.findAll();
    }

    public boolean isGenreExist(int genreId) throws ResponseStatusException {
        boolean genreExist = genresDbStorage.isExistsGenre(genreId);
        if (!genreExist) {
            log.info("Не найден жанр по ID: {}", genreId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден жанр по ID: " + genreId);
        }

        return true;
    }
}