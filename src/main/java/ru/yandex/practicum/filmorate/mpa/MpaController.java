package ru.yandex.practicum.filmorate.mpa;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> getAllMpa(Pageable pageable) {
        return mpaService.getAllMpa(pageable);
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        return mpaService.getMpaById(id);
    }
}