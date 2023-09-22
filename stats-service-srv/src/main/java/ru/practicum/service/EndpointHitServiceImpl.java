package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository ehr;

    //Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем
    @Override
    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(endpointHitDto);
        ehr.save(endpointHit);
    }

    //Получние статистики по посещениям
    @Override
    @Transactional
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endDate = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<ViewStats> stats;

        if (Boolean.TRUE.equals(unique)) {
            stats = ehr.findUniqueIpViewStats(startDate, endDate);
        } else {
            stats = ehr.findNonUniqueIpViewStats(startDate, endDate);
        }

        return uris == null ? stats : stats.stream()
                .map(viewStats -> filterByUris(viewStats, uris))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    //фильтрация статистики по списку URIs
    private ViewStats filterByUris(ViewStats vs, List<String> uris) {
        if (uris.contains(vs.getUri())) {
            return vs;
        } else {
            return null;
        }
    }
}
