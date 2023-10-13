package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    //Получение подбророк событий
    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        log.info("Получены все подборки событий");

        if (pinned == null) {
            return compilationRepository.findAll(pageRequest)
                    .stream()
                    .map(CompilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        }

        List<Compilation> compilationsList = compilationRepository.findAllByPinned(pinned, pageRequest);

        return compilationsList
                .stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    //Получение подброрки событий по его id
    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        log.info("Получение подборки событий с id = {}", compId);
        Compilation compilation = checkCompilationExistence(compId);

        log.info("Получена подборка событий с id = {}", compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    //Добавление новой подборки (подборка может не содержать событий)
    @Override
    @Transactional
    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto) {
        log.info("Добавление новой подборки" );
        List<Event> events = new ArrayList<>();

        List<Long> eventIds = newCompilationDto.getEvents();
        if (eventIds != null) {
            events = eventRepository.findAllByIdIn(eventIds);
        }

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        Compilation savedCompilation = compilationRepository.save(compilation);

        log.info("Подборка с id = {} добавлена.", savedCompilation.getId());
        return CompilationMapper.toCompilationDto(savedCompilation);
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

        log.info("Подборка событий с id = {} обновлена", compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    //проверка существования подборки событий
    private Compilation checkCompilationExistence(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий с id = " + compId + " не найдена"));
    }

    //проверка существования события
    private Event checkEventExistence(Event event) {
        Long eventId = event.getId();
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }
}
