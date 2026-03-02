package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.List;

@Slf4j
@Service
public class MpaService {

    private final MpaRepository mpaRepository;

    public MpaService(MpaRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    public Mpa getMpaById(Long mpaId) {

        return mpaRepository.findById(mpaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Не найден Mpa-возрастное ограничение при запросе на возврат по ID: " + mpaId));
    }

    public List<Mpa> getAllMpa(Pageable pageable) {

        return mpaRepository.findAll(pageable).getContent();
    }

    public void existsMpa(Long mpaId) {

        boolean existsMpa = mpaRepository.existsById(mpaId);
        if (!existsMpa) {
            log.info("Не найден Mpa-возрастное ограничение по ID: {}", mpaId);

            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден Mpa-возрастное ограничение по ID: " + mpaId);
        }
    }
}