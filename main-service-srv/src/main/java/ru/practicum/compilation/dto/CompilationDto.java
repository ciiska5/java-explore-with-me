package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

/**
 * Шаблон объекта CompilationDto для пользователя.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private Long id;//Идентификатор

    private String title;//Заголовок подборки

    private Boolean pinned;//Закреплена ли подборка на главной странице сайта

    private List<EventShortDto> events;//Список событий входящих в подборку
}
