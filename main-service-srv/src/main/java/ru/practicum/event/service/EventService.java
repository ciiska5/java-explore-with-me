package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.repository.parameters.AdminParameters;
import ru.practicum.event.repository.parameters.PublicParameters;
import ru.practicum.event.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    //Получение событий, добавленных текущим пользователем
    List<EventShortDto> getAllEventsOfCurrentUser(Long userId, Integer from, Integer size);

    //Добавление нового события
    EventFullDto addNewEvent(Long userId, NewEventDto newEventDto);

    //Получение полной информации о событии, добавленном текущим пользователем
    EventFullDto getEventOfCurrentUser(Long userId, Long eventId);

    //Изменение события, добавленного текущим пользователем
    EventFullDto updateEventOfCurrentUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    //Получение информации о запросах на участие в событии текущего пользователя
    List<ParticipationRequestDto> getAllRequestsOfCurrentUser(Long userId, Long eventId);

    //Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    EventRequestStatusUpdateResult updateRequestStatusOfCurrentUser(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest
    );

    //Поиск событий (Admin)
    List<EventFullDto> getAllEventsByAdmin(
            AdminParameters adminParameters,
            Integer from,
            Integer size
    );

    //Редактирование данных события и его статуса (отклонение/публикация) (Admin)
    EventFullDto updateEventAndRequestStatusByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    //Получение событий с возможностью фильтрации
    List<EventShortDto> getAllEventsForPublic(
            PublicParameters publicParameters,
            Integer from,
            Integer size,
            HttpServletRequest httpServletRequest
    );

    //Получение подробной информации об опубликованном событии по его идентификатору
    EventFullDto getEventByIdForPublic(Long id, HttpServletRequest httpServletRequest);
}
