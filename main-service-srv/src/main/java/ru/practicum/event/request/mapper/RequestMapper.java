package ru.practicum.event.request.mapper;

import ru.practicum.event.request.dto.ParticipationRequestDto;
import ru.practicum.event.request.model.Request;

import java.time.format.DateTimeFormatter;

public class RequestMapper {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();

        participationRequestDto.setId(request.getId());
        participationRequestDto.setCreated(request.getCreated().format(DATE_TIME_FORMATTER));
        participationRequestDto.setStatus(request.getStatus().toString());
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setRequester(request.getRequester().getId());

        return participationRequestDto;
    }
}
