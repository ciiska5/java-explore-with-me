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
    Long id;//Идентификатор

    String title;//Заголовок подборки

    Boolean pinned;//Закреплена ли подборка на главной странице сайта

    List<EventShortDto> events;//Список событий входящих в подборку
}
