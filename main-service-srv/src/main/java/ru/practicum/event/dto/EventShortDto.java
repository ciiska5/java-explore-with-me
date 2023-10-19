package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

/**
 * Шаблон объекта EventShortDto для пользователя.
 * Краткая информация о событии.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private Long id; //Идентификатор

    private String annotation;//Краткое описание

    private Long confirmedRequests;//Количество одобренных заявок на участие в данном событии

    private String eventDate;//Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    private Boolean paid;//Нужно ли оплачивать участие

    private String title;//Заголовок

    private Long views;//Количество просмотров события

    private UserShortDto initiator;//Пользователь (краткая информация)

    private CategoryDto category;//Категория
}
