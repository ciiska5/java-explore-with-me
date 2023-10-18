package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.dto.ViewStats;
import ru.practicum.event.enums.status.RequestStatus;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.request.model.ConfirmedRequestsCount;
import ru.practicum.event.request.repository.RequestRepository;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    //Получение подбророк событий
    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        log.info("Получены все подборки событий");

        if (pinned == null) {

            List<Compilation> compilationsList = compilationRepository.findAll(pageRequest).toList();

            Set<Event> eventSet = new HashSet<>();
            for (Compilation compilation : compilationsList) {
                eventSet.addAll(compilation.getEvents());
            }
            List<Event> eventList = new ArrayList<>(eventSet);

            Map<Long, Long> viewStats = getViewStats(eventList);
            Map<Long, Long> confirmedRequests = getConfirmedRequestsStats(eventList);

            return CompilationMapper.toCompilationDtoList(compilationsList, viewStats, confirmedRequests);
        }

        List<Compilation> compilationsList = compilationRepository.findAllByPinned(pinned, pageRequest);

        Set<Event> eventSet = new HashSet<>();
        for (Compilation compilation : compilationsList) {
            eventSet.addAll(compilation.getEvents());
        }
        List<Event> eventList = new ArrayList<>(eventSet);

        Map<Long, Long> viewStats = getViewStats(eventList);
        Map<Long, Long> confirmedRequests = getConfirmedRequestsStats(eventList);

        return CompilationMapper.toCompilationDtoList(compilationsList, viewStats, confirmedRequests);
    }

    //Получение подброрки событий по его id
    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        log.info("Получение подборки событий с id = {}", compId);
        Compilation compilation = checkCompilationExistence(compId);

        Map<Long, Long> viewStats = getViewStats(compilation.getEvents());
        Map<Long, Long> confirmedRequests = getConfirmedRequestsStats(compilation.getEvents());

        log.info("Получена подборка событий с id = {}", compId);
        return CompilationMapper.toCompilationDto(compilation, viewStats, confirmedRequests);
    }

    //Добавление новой подборки (подборка может не содержать событий)
    @Override
    @Transactional
    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto) {
        log.info("Добавление новой подборки");
        List<Event> events = new ArrayList<>();

        List<Long> eventIds = newCompilationDto.getEvents();
        if (eventIds != null) {
            events = eventRepository.findAllByIdIn(eventIds);
        }

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        Compilation savedCompilation = compilationRepository.save(compilation);

        Map<Long, Long> viewStats = getViewStats(events);
        Map<Long, Long> confirmedRequests = getConfirmedRequestsStats(events);

        log.info("Подборка с id = {} добавлена.", savedCompilation.getId());
        return CompilationMapper.toCompilationDto(savedCompilation, viewStats, confirmedRequests);
    }

    //Удаление подборки
    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.info("Удаление подборки событий с id = {} ", compId);
        checkCompilationExistence(compId);

        log.info("Подборка событий с id = {} удалена ", compId);
        compilationRepository.deleteById(compId);
    }

    //Обновить информацию о подборке
    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Обновление подборки событий с id = {}", compId);
        Compilation compilation = checkCompilationExistence(compId);

        List<Long> newEventIdsList = updateCompilationRequest.getEvents();
        List<Event> newEventList = null;
        if (newEventIdsList != null) {
            if (newEventIdsList.size() > 0) {
                newEventList = eventRepository.findAllByIdIn(newEventIdsList);
            }
        }

        Boolean newPinned = updateCompilationRequest.getPinned();
        String newTitle = updateCompilationRequest.getTitle();

        if (newPinned != null) {
            compilation.setPinned(newPinned);
        }
        if (newTitle != null) {
            compilation.setTitle(newTitle);
        }
        if (newEventList != null) {
            compilation.setEvents(newEventList);
        }

        Map<Long, Long> viewStats = getViewStats(compilation.getEvents());
        Map<Long, Long> confirmedRequests = getConfirmedRequestsStats(compilation.getEvents());

        log.info("Подборка событий с id = {} обновлена", compId);
        return CompilationMapper.toCompilationDto(compilation, viewStats, confirmedRequests);
    }

    //проверка существования подборки событий
    private Compilation checkCompilationExistence(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий с id = " + compId + " не найдена"));
    }

    //получение статистики <eventId, viewCount> по просмотрам для списка событий
    private Map<Long, Long> getViewStats(List<Event> events) {
        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .collect(Collectors.toList());

        List<ViewStats> viewStats = statsClient.getViewStats(
                timeToString(LocalDateTime.now().minusHours(1)),
                timeToString(LocalDateTime.now().plusSeconds(1)),
                uris,
                true
        );

        if (viewStats.size() > 0) {
            return viewStats.stream()
                    .collect(Collectors.toMap(viewStat -> {
                                String uri = viewStat.getUri();
                                return Long.parseLong(uri.substring(uri.lastIndexOf("/") + 1));
                            },
                            ViewStats::getHits
                    ));
        }
        return Map.of();
    }

    //получение данных <eventId, confirmedRequests> по подтвержденным запросам для списка событий
    private Map<Long, Long> getConfirmedRequestsStats(List<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<ConfirmedRequestsCount> confirmedRequestsList = requestRepository.getConfirmedRequestsCountList(
                eventIds, RequestStatus.CONFIRMED);

        return confirmedRequestsList.stream()
                .collect(Collectors.toMap(
                        ConfirmedRequestsCount::getEventId,
                        ConfirmedRequestsCount::getConfirmedNumber)
                );
    }

    //конвертация времени из LocalDateTime в строку
    private static String timeToString(LocalDateTime time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (time == null) {
            return null;
        }
        return time.format(dateTimeFormatter);
    }
}
