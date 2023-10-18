package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation,
                                                  Map<Long, Long> viewStats,
                                                  Map<Long, Long> confirmedRequests) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();

        List<Event> eventsList = compilation.getEvents();
        if (!eventsList.isEmpty()) {
            eventShortDtoList = EventMapper.toEventShortDtoList(eventsList, viewStats, confirmedRequests);
        }
        CompilationDto compilationDto = new CompilationDto();

        compilationDto.setId(compilation.getId());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setEvents(eventShortDtoList);

        return compilationDto;
    }

    public static List<CompilationDto> toCompilationDtoList(List<Compilation> compilationList,
                                                  Map<Long, Long> viewStats,
                                                  Map<Long, Long> confirmedRequests) {
        return compilationList.stream()
                .map(compilation -> toCompilationDto(compilation, viewStats, confirmedRequests))
                .collect(Collectors.toList());
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
}
