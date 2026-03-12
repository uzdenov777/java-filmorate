package ru.yandex.practicum.filmorate.genre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.genre.model.Genre;
import ru.yandex.practicum.filmorate.genre.model.dto.GenreDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class GenresService {

    private final GenresRepository genresRepository;
    private final GenreMapper genreMapper;

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
            genreIds.add(dto.getId());
        }

        long numberMatches = genresRepository.countByIdIn(genreIds);

        boolean isMatch = numberMatches == genres.size();
        if (!isMatch) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не существует один или несколько жанров");
        }
    }
}