package ru.practicum.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Шаблон объекта EndpointHitDto для пользователя.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {

    @NotBlank
    private String app;//Идентификатор сервиса для которого записывается информация

    @NotBlank
    private String uri;//URI для которого был осуществлен запрос

    @NotBlank
    private String ip;//IP-адрес пользователя, осуществившего запрос

    @NotBlank
    private String timestamp;//Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
}
