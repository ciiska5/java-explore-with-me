package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.repository.parameters.PublicParameters;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* Публичный API для работы с событиями
**/

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventsController {
    private final EventService eventService;

    //Получение событий с возможностью фильтрации.
    @GetMapping
    public List<EventShortDto> getAllEventsForPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpServletRequest) {
        //создаем одну сущность из параметров
        String uri = httpServletRequest.getRequestURI();
        String remoteAddr = httpServletRequest.getRemoteAddr();
        PublicParameters publicParameters = new PublicParameters(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);

        return eventService.getAllEventsForPublic(
                publicParameters, from, size, uri, remoteAddr
        );
    }

    //Получение подробной информации об опубликованном событии по его идентификатору
    @GetMapping("/{id}")
    public EventFullDto getEventByIdForPublic(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        String uri = httpServletRequest.getRequestURI();
        String remoteAddr = httpServletRequest.getRemoteAddr();
        return eventService.getEventByIdForPublic(id, uri, remoteAddr);
    }
}
