package ru.practicum.event.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Шаблон объекта EventRequestStatusUpdateResult для пользователя.
 * Результат подтверждения/отклонения заявок на участие в событии.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;//Одобренные заявки на участие в событии

    private List<ParticipationRequestDto> rejectedRequests;//Отклоненные заявки на участие в событии
}
