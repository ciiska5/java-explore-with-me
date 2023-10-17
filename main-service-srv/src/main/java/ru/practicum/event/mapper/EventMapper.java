package ru.practicum.event.mapper;

import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.enums.states.EventState;
import ru.practicum.event.location.model.LocationDB;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.category.mapper.CategoryMapper.toCategoryDto;
import static ru.practicum.event.location.mapper.LocationMapper.toLocationDto;
import static ru.practicum.user.mapper.UserMapper.toUserShortDto;

public class EventMapper {

    public static EventFullDto toEventFullDto(Event event, Long views) {
        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(toCategoryDto(event.getCategory()));
        eventFullDto.setCreatedOn(timeToString(event.getCreatedOn()));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(timeToString(event.getEventDate()));
        eventFullDto.setInitiator(toUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(toLocationDto(event.getLocation()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(timeToString(event.getPublishedOn()));
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(views);
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());

        return eventFullDto;
    }

    public static EventShortDto toEventShortDto(Event event, Long views) {
        EventShortDto eventShortDto = new EventShortDto();

        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(toCategoryDto(event.getCategory()));
        eventShortDto.setEventDate(timeToString(event.getEventDate()));
        eventShortDto.setInitiator(toUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setViews(views);

        return eventShortDto;
    }

    public static Event toEvent(NewEventDto newEventDto, User user, Category category, LocationDB location) {
        boolean requestModeration = true;
        if (newEventDto.getRequestModeration() != null) {
            requestModeration = newEventDto.getRequestModeration();
        }

        boolean paid = false;
        if (newEventDto.getPaid() != null) {
            paid = newEventDto.getPaid();
        }

        Long participantLimit = 0L;
        if (newEventDto.getParticipantLimit() != null) {
            participantLimit = newEventDto.getParticipantLimit();
        }

        Event event = new Event();

        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(stringToTime(newEventDto.getEventDate()));
        event.setPublishedOn(null);
        event.setCreatedOn(LocalDateTime.now());
        event.setLocation(location);
        event.setPaid(paid);
        event.setParticipantLimit(participantLimit);
        event.setRequestModeration(requestModeration);
        event.setTitle(newEventDto.getTitle());
        event.setInitiator(user);
        event.setConfirmedRequests(0L);
        event.setState(EventState.PENDING);


        return event;
    }

    //конвертация времени из LocalDateTime в строку
    private static String timeToString(LocalDateTime time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (time == null) {
            return null;
        }
        return time.format(dateTimeFormatter);
    }

    //конвертация временм из строки в LocalDateTime
    private static LocalDateTime stringToTime(String stringTime) {
        if (stringTime == null) {
            return null;
        }
        String[] lines = stringTime.split(" ");
        return LocalDateTime.of(LocalDate.parse(lines[0]), LocalTime.parse(lines[1]));
    }
}
