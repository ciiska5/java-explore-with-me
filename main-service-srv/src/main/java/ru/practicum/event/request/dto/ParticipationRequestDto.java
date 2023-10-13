package ru.practicum.event.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Шаблон объекта ParticipationRequestDto для пользователя.
 * Заявка на участие в событии.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    private Long id;//Идентификатор заявки

    private String created;//Дата и время создания заявки

    private String status;//Статус заявки

    private Long event;//Идентификатор события

    private Long requester;//Идентификатор пользователя, отправившего заявку
}
