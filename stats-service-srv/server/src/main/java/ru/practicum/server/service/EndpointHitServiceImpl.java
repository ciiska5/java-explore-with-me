package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.server.mapper.EndpointHitMapper;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    @Transactional(readOnly = true)
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endDate = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return uris == null ?
                (Boolean.TRUE.equals(unique) ? ehr.findUniqueIpAndNonUriViewStats(startDate, endDate)
                        : ehr.findNonUniqueIpAndNonUriViewStats(startDate, endDate))
                :
                (Boolean.TRUE.equals(unique) ? ehr.findUniqueIpAndUriContainingViewStats(startDate, endDate, uris)
                        : ehr.findNonUniqueIpAndContainingViewStats(startDate, endDate, uris));
    }
}
