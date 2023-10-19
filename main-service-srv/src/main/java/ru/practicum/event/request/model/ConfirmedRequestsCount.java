package ru.practicum.event.request.model;

import lombok.*;

/**
 * Вспомогательный класс для подсчета подтаержденных запросов для конкретного ивента
 * в случае использования списка ивентов
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmedRequestsCount {
    private Long eventId;
    private Long confirmedNumber;
}
