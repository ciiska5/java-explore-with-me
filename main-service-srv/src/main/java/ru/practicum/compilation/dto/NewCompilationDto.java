package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Шаблон объекта NewCompilationDto для пользователя.
 * Для добаления новой подборки событий
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    String title;//Заголовок подборки

    Boolean pinned;//Закреплена ли подборка на главной странице сайта

    List<Long> events;//Список идентификаторов событий входящих в подборку
}
