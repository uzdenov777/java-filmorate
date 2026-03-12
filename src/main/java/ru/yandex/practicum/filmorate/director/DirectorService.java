package ru.yandex.practicum.filmorate.director;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.director.model.DirectorDto;

import java.util.Set;

@AllArgsConstructor
@Service
public class DirectorService {

    private final DirectorRepository directorRepository;
    private final DirectorMapper directorMapper;

    public DirectorDto create(DirectorDto dto) {
        var director = directorMapper.toEntity(dto);

        var saved = directorRepository.save(director);
        return directorMapper.toDto(saved);
    }


    public DirectorDto update(DirectorDto dto) {
        var id = dto.getId();

        var exists = directorRepository.existsById(id);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден режиссер для обновления по ID: " + id);
        }

        var director = directorMapper.toEntity(dto);

        var saved = directorRepository.save(director);
        return directorMapper.toDto(saved);
    }

    public DirectorDto getDtoById(Long id) {
        return directorRepository.findById(id)
                .map(directorMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Не найден режиссер для возвращения по ID: " + id));
    }


    public Set<DirectorDto> getAll(Pageable pageable) {
        var directors = directorRepository.findAll(pageable);

        return directorMapper.toDtos(directors);
    }

    public void delete(Long id) {
        var exists = directorRepository.existsById(id);

        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден режиссер для удаления по ID: " + id);
        }

        directorRepository.deleteById(id);
    }

    public void existById(Long directorId) {
        var exists = directorRepository.existsById(directorId);

        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден режиссер по ID: " + directorId);
        }
    }
}