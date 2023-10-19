package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Шаблон объекта UserDto для пользователя.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;//Идентификатор

    private String email;//Почтовый адрес

    private String name;//Имя
}
