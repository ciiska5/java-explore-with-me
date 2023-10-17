package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.location.dto.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Шаблон объекта NewEventDto для пользователя.
 * Для добаления нового события
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;//Краткое описание события

    @NotNull
    @Size(min = 20, max = 7000)
    private String description;//Полное описание события

    @NotNull
    private String eventDate;//Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    private Boolean paid;//Нужно ли оплачивать участие

    private Long participantLimit;//Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    private Boolean requestModeration;//Нужна ли пре-модерация заявок на участие

    @NotNull
    @Size(min = 3, max = 120)
    private String title;//Заголовок события

    @NotNull
    private Long category;//id категории к которой относится событие

    @NotNull
    private Location location;//Широта и долгота места проведения события
}
