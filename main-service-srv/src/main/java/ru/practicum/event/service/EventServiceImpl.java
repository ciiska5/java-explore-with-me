package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.states.EventState;
import ru.practicum.event.enums.states.StateAction;
import ru.practicum.event.enums.status.RequestStatus;
import ru.practicum.event.location.model.LocationDB;
import ru.practicum.event.location.repository.LocationRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.parameters.AdminParameters;
import ru.practicum.event.repository.parameters.PublicParameters;
import ru.practicum.event.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.request.dto.ParticipationRequestDto;
import ru.practicum.event.request.mapper.RequestMapper;
import ru.practicum.event.request.model.ConfirmedRequestsCount;
import ru.practicum.event.request.model.Request;
import ru.practicum.event.request.repository.RequestRepository;
import ru.practicum.exception.EventDateException;
import ru.practicum.exception.EventValidationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RequestValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.event.location.mapper.LocationMapper.toLocation;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    /**
     * Для PRIVATE API
     */
    //Получение событий, добавленных текущим пользователем
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllEventsOfCurrentUser(Long userId, Integer from, Integer size) {
        log.info("Получение событий, добавленных текущим пользователем с id = {}", userId);
        checkUserExistence(userId);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Event> foundEventList = eventRepository.findAllByInitiatorId(userId, pageRequest);
        if (foundEventList.isEmpty()) {
            log.info("Cобытия, добавленные текущим пользователем с id = {}, не найдены", userId);
            return List.of();
        }

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsStats(foundEventList);
        Map<Long, Long> viewsMap = getViewStats(foundEventList);

        log.info("УСПЕШНО получены события, добавленные текущим пользователем с id = {}", userId);
        return EventMapper.toEventShortDtoList(foundEventList, viewsMap, confirmedRequestsMap);
    }

    //Добавление нового события
    @Override
    @Transactional
    public EventFullDto addNewEvent(Long userId, NewEventDto newEventDto) {
        log.info("Добавление пользователем c id = {} нового события", userId);
        String eventDate = newEventDto.getEventDate();
        checkTwoHoursTimeRestriction(eventDate);

        User user = checkUserExistence(userId);
        Category category = checkCategoryExistence(newEventDto.getCategory());

        LocationDB location = locationRepository.save(toLocation(newEventDto.getLocation()));

        Event event = EventMapper.toEvent(newEventDto, user, category, location);
        Event savedEvent = eventRepository.save(event);

        Long views = 0L;
        Long confirmedRequests  = 0L;
        log.info("Пользователем c id = {} УСПЕШНО добвлено новое событие с id = {}", userId, event.getId());
        return EventMapper.toEventFullDto(savedEvent, views, confirmedRequests);
    }

    //Получение полной информации о событии, добавленном текущим пользователем
    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventOfCurrentUser(Long userId, Long eventId) {
        log.info("Получение события c id = {}, добавленного текущим пользователем c id = {}", eventId, userId);
        checkUserExistence(userId);
        checkEventExistence(eventId);
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);
        Long confirmedRequests = requestRepository.countRequestByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        log.info("УСПЕШНО получено событие c id = {}, добавленное текущим пользователем c id = {}", eventId, userId);
        return EventMapper.toEventFullDto(event, getViewStats(event), confirmedRequests);
    }

    //Изменение события, добавленного текущим пользователем
    @Override
    @Transactional
    public EventFullDto updateEventOfCurrentUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Изменение события c id = {}, добавленного текущим пользователем c id = {}", eventId, userId);
        checkUserExistence(userId);
        Event event = checkEventExistence(eventId);

        String eventDate = updateEventUserRequest.getEventDate();
        checkTwoHoursTimeRestriction(eventDate);

        LocationDB updateLocation = null;
        if (updateEventUserRequest.getLocation() != null) {
            updateLocation = locationRepository.save(toLocation(updateEventUserRequest.getLocation()));
        }

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new EventValidationException("Изменить можно только отмененные события или" +
                    " события в состоянии ожидания модерации");
        }
        if (!userId.equals(event.getInitiator().getId())) {
            throw new EventValidationException("Редактировать событие может только его создатель");
        }

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equalsIgnoreCase("SEND_TO_REVIEW")) {
                event.setState(EventState.PENDING);
            } else if (updateEventUserRequest.getStateAction().equalsIgnoreCase("CANCEL_REVIEW")) {
                event.setState(EventState.CANCELED);
            } else {
                throw new EventValidationException("Заданы некорректные данные для параметра stateAction");
            }
        }

        updateEventFieldsByUser(event, updateEventUserRequest, updateLocation);
        Long confirmedRequests = requestRepository.countRequestByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        Event updatedEvent = eventRepository.save(event);
        log.info("УСПЕШНО изменено событие c id = {}, добавленное текущим пользователем c id = {}", eventId, userId);
        return EventMapper.toEventFullDto(updatedEvent, getViewStats(event), confirmedRequests);
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllRequestsOfCurrentUser(Long userId, Long eventId) {
        log.info("Полчение списка запрсов к событию c id = {}, добавленного пользователем c id = {}", eventId, userId);
        checkUserExistence(userId);
        checkEventExistence(eventId);

        List<Request> requestList = requestRepository.findAllByEventId(eventId);

        log.info("УСПЕШНО полчен список запрсов к событию c id = {}, добавленного пользователем c id = {}", eventId, userId);
        return requestList
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    //Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatusOfCurrentUser(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Изменение статуса заявок к событию c id = {}, добавленного пользователем c id = {}", eventId, userId);
        checkUserExistence(userId);
        Event event = checkEventExistence(eventId);

        if (eventRequestStatusUpdateRequest.getStatus() == null) {
            throw new RequestValidationException("Статус не должен быть пустым");
        }

        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        List<Request> requestList = requestRepository.findAllByIdInAndStatus(requestIds, RequestStatus.CONFIRMED);
        if (requestList.size() > 0 && eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED.toString())) {
            throw new RequestValidationException("Cтатус можно изменить только у заявок, находящихся в состоянии ожидания");
        }

        if (!userId.equals(event.getInitiator().getId())) {
            throw new RequestValidationException("Запрещено редактировать чужое событие");
        }

        if (!event.getRequestModeration()) {
            throw new RequestValidationException("Подтверждение заявок не требуется");
        }

        Long participationLimit = event.getParticipantLimit();
        Long confirmedRequests = requestRepository.countRequestByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED.toString())) {
            long leftRequests = participationLimit - confirmedRequests;
            if (leftRequests <= 0) {
                throw new RequestValidationException("Лимит заявок равен 0");
            }

            //обновление статуса запросов на CONFIRMED в случае наличия свободных мест на все запроосы
            if (event.getParticipantLimit() == null || leftRequests >= requestIds.size()) {
                List<Long> confirmedRequestList = eventRequestStatusUpdateRequest.getRequestIds();
                requestRepository.updateRequests(confirmedRequestList, RequestStatus.CONFIRMED, RequestStatus.PENDING);
                return getUpdatedRequestResult(confirmedRequestList, new ArrayList<>());
            } else { //обновление статуса запросов на CONFIRMED и REJECTED при исчерпании лимита участников
                List<Long> confirmed = new ArrayList<>();
                List<Long> rejected = new ArrayList<>();
                for (long id : requestIds) {
                    if (leftRequests > 0) {
                        confirmed.add(id);
                        leftRequests--;
                    } else {
                        rejected.add(id);
                    }
                }
                if (!confirmed.isEmpty()) {
                    requestRepository.updateRequests(confirmed, RequestStatus.CONFIRMED, RequestStatus.PENDING);
                }
                if (!rejected.isEmpty()) {
                    requestRepository.updateRequests(rejected, RequestStatus.REJECTED, RequestStatus.PENDING);
                }
                return getUpdatedRequestResult(confirmed, rejected);
            }
        //при обновлении статуса запросов на REJECTED
        } else if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED.toString())) {
            List<Long> rejectedRequests = eventRequestStatusUpdateRequest.getRequestIds();
            requestRepository.updateRequests(requestIds, RequestStatus.REJECTED, RequestStatus.PENDING);
            return getUpdatedRequestResult(new ArrayList<>(), rejectedRequests);
        } else {
            throw new RequestValidationException("Задан неизвестный статус запроса");
        }
    }

    /**
     * Для ADMIN API
     */
    //Поиск событий (Admin)
    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllEventsByAdmin(
            AdminParameters adminParameters,
            Integer from,
            Integer size) {
        log.info("Получение всех событий с параметрами = {}", adminParameters);
        LocalDateTime rangeStart = stringToTime(adminParameters.getRangeStart());
        LocalDateTime rangeEnd = stringToTime(adminParameters.getRangeEnd());
        checkTimeParameters(rangeStart, rangeEnd);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(5000);
        }

        List<EventState> eventStateList = adminParameters.getStates() == null ? null : adminParameters.getStates()
                .stream().map(EventState::valueOf).collect(Collectors.toList());

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> foundEvents = eventRepository.getEventsByAdmin(
                adminParameters, rangeStart, rangeEnd, eventStateList, pageRequest
        );

        if (foundEvents.isEmpty()) {
            log.info("События с параметрами = {} не найдены", adminParameters);
            return List.of();
        }

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsStats(foundEvents);
        Map<Long, Long> viewsMap = getViewStats(foundEvents);

        log.info("Получены все события с параметрами = {}", adminParameters);

        return  EventMapper.toEventFullDtoList(foundEvents, viewsMap, confirmedRequestsMap);
    }

    //Редактирование данных события и его статуса (отклонение/публикация) (Admin)
    @Override
    @Transactional
    public EventFullDto updateEventAndRequestStatusByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Редактирование данных события c id = {}", eventId);
        Event event = checkEventExistence(eventId);

        Category category = null;
        if (updateEventAdminRequest.getCategory() != null) {
            category = checkCategoryExistence(updateEventAdminRequest.getCategory());
        }

        LocationDB updateLocation = null;
        if (updateEventAdminRequest.getLocation() != null) {
            updateLocation = locationRepository.save(toLocation(updateEventAdminRequest.getLocation()));
        }

        if (!event.getState().equals(EventState.PENDING)) {
            throw new EventValidationException(
                    "Событие можно публиковать, только если оно в состоянии ожидания публикации"
            );
        }

        String updatedRequestState = updateEventAdminRequest.getStateAction();
        if (updatedRequestState != null) {
            if (updatedRequestState.equals(StateAction.PUBLISH_EVENT.toString())) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new EventValidationException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
                }
                event.setPublishedOn(LocalDateTime.now());
                event.setState(EventState.PUBLISHED);
            }
            if (updatedRequestState.equals(StateAction.REJECT_EVENT.toString())) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new EventValidationException("Событие можно отклонить, только если оно еще не опубликовано");
                }
                event.setState(EventState.CANCELED);
            }
        }

        updateEventFieldsByAdmin(event, updateEventAdminRequest, updateLocation, category);

        checkOneHoursTimePublicationRestriction(event.getEventDate());

        Long confirmedRequest = requestRepository.countRequestByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        log.info("Отредактированы данные события c id = {}", eventId);
        return EventMapper.toEventFullDto(event, getViewStats(event), confirmedRequest);
    }

    /**
     * Для PUBLIC API
     */
    //Получение событий с возможностью фильтрации
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllEventsForPublic(PublicParameters publicParameters,
                                                      Integer from,
                                                      Integer size,
                                                      String uri,
                                                      String remoteAddr) {
        log.info("Получение событий с параметрами {}", publicParameters);

        LocalDateTime rangeStart = stringToTime(publicParameters.getRangeStart());
        LocalDateTime rangeEnd = stringToTime(publicParameters.getRangeEnd());
        checkTimeParameters(rangeStart, rangeEnd);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(5000);
        }

        String sortParam = checkSortParameter(publicParameters.getSort());
        publicParameters.setSort(sortParam);

        String text = publicParameters.getText();
        if (text != null) {
            publicParameters.setText(text.toLowerCase());
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (publicParameters.getOnlyAvailable()) {
            List<Event> availableEventList = eventRepository.searchAvailableEventsByPublic(
                    publicParameters, rangeStart, rangeEnd, pageRequest
            );
            saveHit(uri, remoteAddr);

            Map<Long, Long> viewStats = getViewStats(availableEventList);
            Map<Long, Long> confirmedRequests = getConfirmedRequestsStats(availableEventList);

            log.info("Получены события с параметрами {}, у которых не исчерпан лимит запросов на участие", publicParameters);
            return EventMapper.toEventShortDtoList(availableEventList, viewStats, confirmedRequests);
        } else {
            List<Event> allPublishedEvents = eventRepository.getAllPublishedEvents(
                    publicParameters, rangeStart, rangeEnd, pageRequest
            );
            saveHit(uri, remoteAddr);

            Map<Long, Long> viewStats = getViewStats(allPublishedEvents);
            Map<Long, Long> confirmedRequests = getConfirmedRequestsStats(allPublishedEvents);

            log.info("Получены все опубликованные события с параметрами {}", publicParameters);
            return EventMapper.toEventShortDtoList(allPublishedEvents, viewStats, confirmedRequests);
        }
    }

    //Получение подробной информации об опубликованном событии по его идентификатору
    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdForPublic(Long id, String uri, String remoteAddr) {
        log.info("Получение подробной информации об опубликованном событии с id = {}", id);
        Event event = checkEventExistence(id);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие должно быть опубликовано");
        }

        saveHit(uri, remoteAddr);
        Long confirmedRequest = requestRepository.countRequestByEventIdAndStatus(id, RequestStatus.CONFIRMED);

        return EventMapper.toEventFullDto(event, getViewStats(event), confirmedRequest);
    }

    /**
     *Вспомогательные методы
     */
    //проверка существования пользователя
    private User checkUserExistence(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    //проверка существования категории
    private Category checkCategoryExistence(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + " не найдена"));
    }

    //конвертация временм из строки в LocalDateTime
    private LocalDateTime stringToTime(String stringTime) {
        if (stringTime == null) {
            return null;
        }
        String[] lines = stringTime.split(" ");
        return LocalDateTime.of(LocalDate.parse(lines[0]), LocalTime.parse(lines[1]));
    }

    //проверка события на то, что его начало не раньше, чем через два часа от текущего момента
    private void checkTwoHoursTimeRestriction(String eventDate) {
        LocalDateTime eventTime = stringToTime(eventDate);
        if (eventTime != null && eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventDateException("Дата и время, на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента");
        }
    }

    //проверка события на то, что дата его начала не ранее чем за час от даты публикации
    public void checkOneHoursTimePublicationRestriction(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().minusHours(1))) {
            throw new EventDateException("Дата начала изменяемого события должна быть " +
                    "не ранее чем за час от даты публикации.");
        }
    }

    //проверка существования события
    private Event checkEventExistence(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    //обновление значения полей события
    private void updateEventFieldsByUser(Event event,
                                         UpdateEventUserRequest updateEventUserRequest,
                                         LocationDB location) {

        String annotation = updateEventUserRequest.getAnnotation();
        Long category = updateEventUserRequest.getCategory();
        String description = updateEventUserRequest.getDescription();
        String eventDate = updateEventUserRequest.getEventDate();
        Boolean paid = updateEventUserRequest.getPaid();
        Long participantLimit = updateEventUserRequest.getParticipantLimit();
        String title = updateEventUserRequest.getTitle();
        Boolean requestModeration = updateEventUserRequest.getRequestModeration();

        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (category != null) {
            event.setCategory(checkCategoryExistence(category));
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (eventDate != null) {
            event.setEventDate(stringToTime(eventDate));
        }
        if (location != null) {
            event.setLocation(location);
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (title != null) {
            event.setTitle(title);
        }
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
    }

    //получение запроса с обновленным статусом
    private EventRequestStatusUpdateResult getUpdatedRequestResult(List<Long> confirmedIds, List<Long> rejectedIds) {
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        if (!confirmedIds.isEmpty()) {
            confirmedRequests = requestRepository.findAllByIdInAndStatus(confirmedIds, RequestStatus.CONFIRMED)
                    .stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
        }
        if (!rejectedIds.isEmpty()) {
            rejectedRequests = requestRepository.findAllByIdInAndStatus(rejectedIds, RequestStatus.REJECTED)
                    .stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    //проверка на то, что дата начала сортировки раньше даты окончания
    private void checkTimeParameters(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new EventDateException(
                    String.format("Заданы некорректные параметры сортировки по времени rangeStart = %s, rangeEnd = %s. " +
                            "rangeStart должен быть раньше rangeEnd", rangeStart, rangeEnd));
        }
    }

    //обновление значения полей события администратором
    private void updateEventFieldsByAdmin(Event event,
                                          UpdateEventAdminRequest updateEventAdminRequest,
                                          LocationDB location,
                                          Category category) {
        String annotation = updateEventAdminRequest.getAnnotation();
        String description = updateEventAdminRequest.getDescription();
        String eventDate = updateEventAdminRequest.getEventDate();
        Boolean paid = updateEventAdminRequest.getPaid();
        Long participantLimit = updateEventAdminRequest.getParticipantLimit();
        String title = updateEventAdminRequest.getTitle();
        Boolean requestModeration = updateEventAdminRequest.getRequestModeration();

        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (category != null) {
            event.setCategory(category);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (eventDate != null) {
            event.setEventDate(stringToTime(eventDate));
        }
        if (location != null) {
            event.setLocation(location);
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (title != null) {
            event.setTitle(title);
        }
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
    }

    //проверка параметра сортировки
    private String checkSortParameter(String sort) {
        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                return "eventDate";
            } else if (sort.equals("VIEWS")) {
                return "views";
            } else {
                throw new EventValidationException("Задан неверный параметр сортировки");
            }
        }
        return "id";
    }

    //сохранить в сервисе статистики информации о том, что по этому эндпоинту был осуществлен и обработан запрос
    private void saveHit(String uri, String remoteAddr) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setUri(uri);
        endpointHitDto.setIp(remoteAddr);
        endpointHitDto.setTimestamp(timeToString(LocalDateTime.now()));
        statsClient.saveHit(endpointHitDto);
    }

    //получение статистики по просмотрам для одного события
    private Long getViewStats(Event event) {
        String uri = "/events/" + event.getId();
        List<String> uris = List.of(uri);

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

    //получение статистики <eventId, viewCount> по просмотрам для списка событий
    private Map<Long, Long> getViewStats(List<Event> events) {
        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .collect(Collectors.toList());

        List<ViewStats> viewStats = statsClient.getViewStats(
                timeToString(LocalDateTime.now().minusHours(1)),
                timeToString(LocalDateTime.now().plusSeconds(1)),
                uris,
                true
        );

        if (viewStats.size() > 0) {
            return viewStats.stream()
                    .collect(Collectors.toMap(viewStat -> {
                            String uri = viewStat.getUri();
                            return Long.parseLong(uri.substring(uri.lastIndexOf("/") + 1));
                        },
                        ViewStats::getHits
                    ));
        }
        return Map.of();
    }

    //получение данных <eventId, confirmedRequests> по подтвержденным запросам для списка событий
    private Map<Long, Long> getConfirmedRequestsStats(List<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<ConfirmedRequestsCount> confirmedRequestsList = requestRepository.getConfirmedRequestsCountList(
                eventIds, RequestStatus.CONFIRMED);

        return confirmedRequestsList.stream()
                .collect(Collectors.toMap(
                        ConfirmedRequestsCount::getEventId,
                        ConfirmedRequestsCount::getConfirmedNumber)
                );
    }

    //конвертация времени из LocalDateTime в строку
    private String timeToString(LocalDateTime time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (time == null) {
            return null;
        }
        return time.format(dateTimeFormatter);
    }
}
