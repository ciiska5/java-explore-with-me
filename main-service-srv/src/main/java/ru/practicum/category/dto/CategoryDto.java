package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Шаблон объекта CategoryDto для пользователя.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id; //Идентификатор категории

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;//Название категории
}
