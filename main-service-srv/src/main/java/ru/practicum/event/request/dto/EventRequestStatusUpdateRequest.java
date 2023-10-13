package ru.practicum.event.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Шаблон объекта EventRequestStatusUpdateRequest для пользователя.
 * Изменение статуса запроса на участие в событии текущего пользователя.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {
    @NotNull
    private List<Long> requestIds;//Идентификаторы запросов на участие в событии текущего пользователя

    @NotNull
    private String status;//Новый статус запроса на участие в событии текущего пользователя
}
