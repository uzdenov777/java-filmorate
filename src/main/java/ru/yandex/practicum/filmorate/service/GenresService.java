package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenresRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GenresService {

    private final GenresRepository genresRepository;

    public GenresService(GenresRepository genresRepository) {
        this.genresRepository = genresRepository;
    }

    public Genre getGenreById(Long genreId) throws ResponseStatusException {

        Optional<Genre> genreOpt = genresRepository.findById(genreId);
        if (genreOpt.isEmpty()) {
            log.info("Не найден жанр при запросе на возврат по ID: {}", genreId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден жанр при запросе на возврат по ID: " + genreId);
        }

        Genre genre = genreOpt.get();
        return genre;
    }

    public List<Genre> getGenres(List<Genre> genres) {

        List<Long> genreIds = new ArrayList<>();
        for (Genre genre : genres) {
            Long genreId = genre.getId();
            genreIds.add(genreId);
        }

        List<Genre> fullGenres = genresRepository.findAllByIdInBatch(genreIds);

        return fullGenres;
    }

    public List<Genre> getAllGenres() {
        return genresRepository.findAll();
    }

    public boolean isGenreExist(Long genreId) throws ResponseStatusException {

        boolean genreExist = genresRepository.existsById(genreId);
        if (!genreExist) {
            log.info("Не найден жанр по ID: {}", genreId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден жанр по ID: " + genreId);
        }

        return true;
    }
}