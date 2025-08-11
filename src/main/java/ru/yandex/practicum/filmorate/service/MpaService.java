package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

@Service
public class MpaService {
    private final MpaService mpaService;

    public MpaService(MpaService mpaService) {
        this.mpaService = mpaService;
    }
}
