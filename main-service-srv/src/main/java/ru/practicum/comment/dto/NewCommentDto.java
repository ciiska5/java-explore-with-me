package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Шаблон объекта NewCommentDto для добавления и обновления комментария
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {
    @NotEmpty
    @NotBlank
    @Size(max = 1500)
    private String text;//Содержимое комментария пользователя
}
