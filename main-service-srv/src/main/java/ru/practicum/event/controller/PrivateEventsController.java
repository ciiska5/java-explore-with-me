package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.request.dto.ParticipationRequestDto;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

/**
 * Закрытый API для работы с событиями
 **/

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class PrivateEventsController {
    private final EventService eventService;

    //Получение событий, добавленных текущим пользователем
    @GetMapping
    public List<EventShortDto> getAllEventsOfCurrentUser(@PathVariable Long userId,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getAllEventsOfCurrentUser(userId, from, size);
    }

    //Добавление нового события
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEvent(@PathVariable Long userId,
                                    @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.addNewEvent(userId, newEventDto);
    }

    //Получение полной информации о событии, добавленном текущим пользователем
    @GetMapping("/{eventId}")
    public EventFullDto getEventOfCurrentUser(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        return eventService.getEventOfCurrentUser(userId, eventId);
    }

    //Изменение события, добавленного текущим пользователем
    @PatchMapping("/{eventId}")
    public EventFullDto updateEventOfCurrentUser(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEventOfCurrentUser(userId, eventId, updateEventUserRequest);
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestsOfCurrentUser(@PathVariable Long userId,
                                                                  @PathVariable Long eventId) {
        return eventService.getAllRequestsOfCurrentUser(userId, eventId);
    }

    //Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatusOfCurrentUser(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return eventService.updateRequestStatusOfCurrentUser(userId, eventId, eventRequestStatusUpdateRequest);
    }

}
