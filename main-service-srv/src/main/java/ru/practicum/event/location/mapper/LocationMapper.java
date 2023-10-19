package ru.practicum.event.location.mapper;

import ru.practicum.event.location.dto.Location;
import ru.practicum.event.location.model.LocationDB;

public class LocationMapper {
    public static Location toLocationDto(LocationDB location) {
        Location locationDto = new Location();

        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());

        return locationDto;
    }

    public static LocationDB toLocation(Location locationDto) {
        LocationDB location = new LocationDB();

        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());

        return location;
    }
}
