package ru.practicum.event.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.enums.states.EventState;
import ru.practicum.event.enums.status.RequestStatus;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.request.dto.ParticipationRequestDto;
import ru.practicum.event.request.mapper.RequestMapper;
import ru.practicum.event.request.model.Request;
import ru.practicum.event.request.repository.RequestRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RequestValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getParticipationRequestsOfUser(Long userId) {
        log.info("Получение списка запросов на участие в чужих событиях для пользователем с id = {}", userId);
        checkUserExistence(userId);

        List<Request> requestList = requestRepository.findAllByRequesterId(userId);

        log.info("Получен список запросов на участие в чужих событиях для пользователем с id = {}", userId);
        return requestList
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    //Добавление запроса от текущего пользователя на участие в событии
    @Override
    @Transactional
    public ParticipationRequestDto addParticipationRequestOfUser(Long userId, Long eventId) {
        log.info(
                "Добавление запроса от пользователя с userId = {} на участие в событии с eventId = {}", userId, eventId
        );
        User requester = checkUserExistence(userId);
        Event event = checkEventExistence(eventId);
        Request request = requestRepository.findByRequesterIdAndEventId(userId, eventId).orElse(null);

        if (event.getInitiator().getId().equals(requester.getId())) {
            throw new RequestValidationException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new RequestValidationException("Нельзя участвовать в неопубликованном событии");
        }
        long confirmedRequest = requestRepository.countRequestByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0 && confirmedRequest >= event.getParticipantLimit()) {
            throw new RequestValidationException("У события достигнут лимит запросов на участие");
        }
        if (request != null) {
            throw new RequestValidationException("Нельзя добавить повторный запрос");
        }

        Request newRequest = new Request();
        newRequest.setEvent(event);
        newRequest.setRequester(requester);
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setStatus(RequestStatus.PENDING);


        //если для события отключена пре-модерация запросов на участие,
        //то запрос должен автоматически перейти в состояние подтвержденного
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
        }

        log.info(
                "Добавлен запроса от пользователя с userId = {} на участие в событии с eventId = {}", userId, eventId
        );
        return RequestMapper.toParticipationRequestDto(requestRepository.save(newRequest));
    }

    //Отмена своего запроса на участие в событии
    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequestByUser(Long userId, Long requestId) {
        log.info(
                "Отмена пользователем с userId = {} своего запроса на участие в событии с requestId = {}", userId, requestId
        );
        checkUserExistence(userId);
        Request request = checkRequestExistence(requestId);

        if (!userId.equals(request.getRequester().getId())) {
            throw new RequestValidationException("Можно отменить только собственную заявку");
        }
        if (request.getStatus().equals(RequestStatus.REJECTED) || request.getStatus().equals(RequestStatus.CANCELED)) {
            throw new RequestValidationException("Заявка с requestId = " + requestId + " уже отклонена либо отменена");
        }

        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);
        log.info(
                "Пользователь с userId = {} успешно отменил свой запроса на участие в событии с requestId = {}", userId, requestId
        );
        return RequestMapper.toParticipationRequestDto(request);
    }

    //проверка существования пользователя
    private User checkUserExistence(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    //проверка существования события
    private Event checkEventExistence(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    //проверка существования запроса
    private Request checkRequestExistence(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " не найден"));
    }
}
