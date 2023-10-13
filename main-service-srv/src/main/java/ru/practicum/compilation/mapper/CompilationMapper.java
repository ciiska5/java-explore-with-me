package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();

        List<Event> eventsList = compilation.getEvents();
        if (!eventsList.isEmpty()) {
            eventShortDtoList = eventsList.stream()
                    .map(EventMapper::toEventShortDto)
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
}
