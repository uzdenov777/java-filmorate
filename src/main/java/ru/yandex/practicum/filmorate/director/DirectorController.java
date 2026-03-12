package ru.yandex.practicum.filmorate.director;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.director.model.DirectorDto;

import java.util.Set;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    public DirectorDto createDirector(@RequestBody DirectorDto directorDto) {
        log.info("Добавление нового режиссера: {}", directorDto);

        return directorService.create(directorDto);
    }

    @PutMapping
    public DirectorDto updateDirector(@RequestBody DirectorDto directorDto) {
        log.info("Обновление нового режиссера: {}", directorDto);

        return directorService.update(directorDto);
    }

    @GetMapping("/{id}")
    public DirectorDto getDirector(@PathVariable Long id) {
        log.info("Вернуть режиссера по ID: {}", id);

        return directorService.getDtoById(id);
    }

    @GetMapping
    public Set<DirectorDto> getDirectors(Pageable pageable) {
        log.info("Возвращение всех режиссеров");

        return directorService.getAll(pageable);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Long id) {
        log.info("Удаление режиссера по ID: {}", id);

        directorService.delete(id);
    }
}