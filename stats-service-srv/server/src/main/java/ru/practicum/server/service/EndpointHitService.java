package ru.practicum.server.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;

import java.util.List;

public interface EndpointHitService {
    //Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем
    void saveHit(EndpointHitDto endpointHitDto);

    //Получние статистики по посещениям
    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}
