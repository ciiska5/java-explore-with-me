package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.repository.parameters.AdminParameters;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

/**
 * API для работы с событиями
 **/

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminEventsController {
    private final EventService eventService;

    //Поиск событий (Admin)
    @GetMapping
    public List<EventFullDto> getAllEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        //создаем одну сущность из параметров
        AdminParameters adminParameters = new AdminParameters(
                users, states, categories, rangeStart, rangeEnd);

        return eventService.getAllEventsByAdmin(adminParameters, from, size);
    }

    @PatchMapping("/{eventId}")
    //Редактирование данных события и его статуса (отклонение/публикация) (Admin)
    public EventFullDto updateEventAndRequestStatusByAdmin(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventAndRequestStatusByAdmin(eventId, updateEventAdminRequest);
    }

}
