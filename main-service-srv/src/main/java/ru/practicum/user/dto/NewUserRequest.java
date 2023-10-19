package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Шаблон объекта NewUserRequest для пользователя.
 * Используется при добавление данных нового пользователя
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotNull
    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private String email;//Почтовый адрес

    @NotBlank
    @NotNull
    @Size(min = 2, max = 250)
    private String name;//Имя
}
