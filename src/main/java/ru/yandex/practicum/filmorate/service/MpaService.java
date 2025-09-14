package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Mpa getMpaById(int mpaId) throws ResponseStatusException {
        Optional<Mpa> mpaOpt = mpaDbStorage.findById(mpaId);
        if (mpaOpt.isEmpty()) {
            log.info("Не найден Mpa-возрастное ограничение при запросе на возврат по ID: {}", mpaId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден Mpa-возрастное ограничение при запросе на возврат по ID: " + mpaId);
        }

        Mpa mpa = mpaOpt.get();
        return mpa;
    }

    public List<Mpa> getAllMpa() {
        return mpaDbStorage.findAll();
    }

    public boolean isExistsMpa(Integer mpaId) throws ResponseStatusException {
        boolean existsMpa = mpaDbStorage.isExistsMpa(mpaId);
        if (!existsMpa) {
            log.info("Не найден Mpa-возрастное ограничение по ID: {}", mpaId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден Mpa-возрастное ограничение по ID: " + mpaId);
        }

        return true;
    }
}