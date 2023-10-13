package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Шаблон объекта ViewStats для пользователя.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStats {
    private String app;//Название сервиса

    private String uri;//URI сервиса

    private Long hits;//Количество просмотров

}
