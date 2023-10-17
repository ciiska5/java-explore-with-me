package ru.practicum.event.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Шаблон объекта LocationDto для пользователя.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    private Float lat;//Широта

    private Float lon;//Долгота
}
