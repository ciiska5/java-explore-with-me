package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Шаблон объекта CommentDto для пользователя.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id; //Идентификатор

    @NotEmpty
    @NotBlank
    private String text;//Содержимое комментария пользователя

    private String created;//Дата и время создания комментария (в формате "yyyy-MM-dd HH:mm:ss")

    private UserShortDto commentator;//Пользователь, оставивший комментарий

    private EventShortDto event;//Событие, к которому пользователь оставил комментарий
}
