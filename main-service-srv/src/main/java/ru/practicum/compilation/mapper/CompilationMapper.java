package ru.practicum.compilation.mapper;

import ru.practicum.client.StatsClient;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.dto.ViewStats;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation, StatsClient statsClient) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();

        List<Event> eventsList = compilation.getEvents();
        if (!eventsList.isEmpty()) {
            eventShortDtoList = eventsList.stream()
                    .map(event -> EventMapper.toEventShortDto(event, getViewStats(event, statsClient)))
                    .collect(Collectors.toList());
        }
        CompilationDto compilationDto = new CompilationDto();

        compilationDto.setId(compilation.getId());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setEvents(eventShortDtoList);

        return compilationDto;
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> eventList) {
        Boolean pinned = false;//состояние по умолчанию

        Boolean updatePinned = newCompilationDto.getPinned();
        if (updatePinned != null) {
            pinned = updatePinned;
        }

        Compilation compilation = new Compilation();

        compilation.setPinned(pinned);
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setEvents(eventList);

        return compilation;
    }

    //получение статистики
    private static Long getViewStats(Event event, StatsClient statsClient) {
        String uri = "/events/" + event.getId();
        String[] uris = new String[] {uri};

        List<ViewStats> viewStats = statsClient.getViewStats(
                timeToString(event.getCreatedOn()),
                timeToString(LocalDateTime.now().plusSeconds(1)),
                uris,
                true
        );

        if (viewStats.size() > 0) {
            return viewStats.get(0).getHits();
        }
        return 0L;
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
