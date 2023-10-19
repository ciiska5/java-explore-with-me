package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Шаблон объекта UserShortDto для пользователя в виде краткой информации.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {
    private Long id;//Идентификатор

    private String name;//Имя
}
