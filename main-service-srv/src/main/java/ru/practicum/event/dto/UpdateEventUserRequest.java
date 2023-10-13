package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.location.model.Location;

import javax.validation.constraints.Size;

/**
 * Шаблон объекта UpdateEventUserRequest.
 * Данные для изменения информации о событии. Если поле в запросе не указано (равно null) -
 * значит изменение этих данных не треубется.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    private String annotation;//Новая аннотация

    @Size(min = 20, max = 7000)
    private String description;//Новое описание

    private String eventDate;//Новые дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"

    private Boolean paid;//Новое значение флага о платности мероприятия

    private Long participantLimit;//Новый лимит пользователей

    private Boolean requestModeration;//Нужна ли пре-модерация заявок на участие

    private String stateAction;//Новое состояние события

    @Size(min = 3, max = 120)
    private String title;//Новый заголовок

    private Long category;//Новая категория

    private Location location;//Широта и долгота места проведения события
}
