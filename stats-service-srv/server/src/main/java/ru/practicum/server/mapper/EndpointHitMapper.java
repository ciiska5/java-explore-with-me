package ru.practicum.server.mapper;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EndpointHitMapper {
    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();

        endpointHitDto.setApp(endpointHit.getApp());
        endpointHitDto.setUri(endpointHit.getUri());
        endpointHitDto.setIp(endpointHit.getIp());
        endpointHitDto.setTimestamp(endpointHit.getTimestamp().toString());

        return endpointHitDto;
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();

        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setIp(endpointHitDto.getIp());
        endpointHit.setTimestamp(
                LocalDateTime.parse(endpointHitDto.getTimestamp(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        return endpointHit;
    }

    public static ViewStats toViewStats(EndpointHit endpointHit) {
        ViewStats viewStats = new ViewStats();

        viewStats.setApp(endpointHit.getApp());
        viewStats.setUri(endpointHit.getUri());

        return viewStats;
    }
}
