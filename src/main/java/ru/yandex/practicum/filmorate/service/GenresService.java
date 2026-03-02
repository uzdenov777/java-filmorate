package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.dto.GenreDto;
import ru.yandex.practicum.filmorate.repository.GenresRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class GenresService {

    private final GenresRepository genresRepository;
    private final GenreMapper genreMapper;

    public GenresService(GenresRepository genresRepository, GenreMapper genreMapper) {
        this.genresRepository = genresRepository;
        this.genreMapper = genreMapper;
    }

    public GenreDto getGenreById(Long genreId) {

        return genresRepository.findById(genreId)
                .map(genreMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND
                        , "Не найден жанр при запросе на возврат по ID: " + genreId));
    }

    public List<GenreDto> getAllGenres(Pageable pageable) {
        Page<Genre> genres = genresRepository.findAll(pageable);

        return genreMapper.toDtos(genres);
    }

    public void allGenresExistByIds(Set<GenreDto> genres) {

        Set<Long> genreIds = new HashSet<>();
        for (GenreDto dto : genres) {
            Long genreId = dto.getId();
            genreIds.add(genreId);
        }

        long numberMatches = genresRepository.countByIdIn(genreIds);

        boolean isMatch = numberMatches == genres.size();
        if (!isMatch) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не существует один или несколько жанров");
        }
    }
}