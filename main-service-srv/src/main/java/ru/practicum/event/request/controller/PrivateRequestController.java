package ru.practicum.event.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.request.dto.ParticipationRequestDto;
import ru.practicum.event.request.service.RequestService;

import java.util.List;

/**
 * Закрытый API для работы с запросами текущего пользователя на участие в событиях
 */

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService requestService;

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    @GetMapping
    public List<ParticipationRequestDto> getParticipationRequestsOfUser(@PathVariable Long userId) {
        return requestService.getParticipationRequestsOfUser(userId);
    }

    //Добавление запроса от текущего пользователя на участие в событии
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationRequestOfUser(@PathVariable Long userId,
                                                                 @RequestParam Long eventId) {
        return requestService.addParticipationRequestOfUser(userId, eventId);
    }

    //Отмена своего запроса на участие в событии
    public @PatchMapping("/{requestId}/cancel")
    ParticipationRequestDto cancelParticipationRequestByUser(@PathVariable Long userId,
                                                             @PathVariable Long requestId) {
        return requestService.cancelParticipationRequestByUser(userId, requestId);
    }

}
