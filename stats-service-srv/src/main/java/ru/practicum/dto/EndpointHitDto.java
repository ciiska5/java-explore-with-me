package ru.practicum.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Шаблон объекта EndpointHitDto для пользователя.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {

    private Long id; //Идентификатор записи

    private String app;//Идентификатор сервиса для которого записывается информация

    private String uri;//URI для которого был осуществлен запрос

    private String ip;//IP-адрес пользователя, осуществившего запрос

    private String timestamp;//Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
}
