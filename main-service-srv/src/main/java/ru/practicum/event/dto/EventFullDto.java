package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.enums.states.EventState;
import ru.practicum.event.location.model.Location;
import ru.practicum.user.dto.UserShortDto;

/**
 * Шаблон объекта EventFullDto для пользователя.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private Long id; //Идентификатор

    private String annotation;//Краткое описание

    private String createdOn;//Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    private Long confirmedRequests;//Количество одобренных заявок на участие в данном событии

    private String description;//Полное описание события

    private String eventDate;//Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    private Boolean paid;//Нужно ли оплачивать участие

    private Long participantLimit;//Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    private String publishedOn;//Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    private Boolean requestModeration;//Нужна ли пре-модерация заявок на участие

    private EventState state;//Список состояний жизненного цикла события

    private String title;//Заголовок

    private Long views;//Количество просмотров события

    private CategoryDto category;//Категория

    private UserShortDto initiator;//Пользователь (краткая информация)

    private Location location;//Широта и долгота места проведения события
}
