package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.service.EndpointHitService;

import java.util.List;

/**
 * Класс-контроллер сущности EndpointHit
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class EndpointHitController {
    private final EndpointHitService ehs;

    //Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
    @PostMapping("/hit")
    public void saveHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Сохранен hit для URI {}", endpointHitDto.getUri());
        ehs.saveHit(endpointHitDto);
    }

    //Получение статистики по посещениям
    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false", required = false) Boolean unique) {
        log.info("Получена статистика для URIs = {}", uris);
        return ehs.getStats(start, end, uris, unique);
    }
}
