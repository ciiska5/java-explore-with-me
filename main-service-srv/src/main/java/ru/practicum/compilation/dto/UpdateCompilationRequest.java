package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * Шаблон объекта UpdateCompilationRequest для пользователя.
 * Изменение информации о подборке событий.
 * Если поле в запросе не указано (равно null) - значит изменение
 * этих данных не треубется.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50)
    String title;//Заголовок подборки

    Boolean pinned;//Закреплена ли подборка на главной странице сайта

    List<Long> events;//Список id событий подборки для полной замены текущего списка
}
