package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    //Получение комментариев к событию
    List<CommentDto> getCommentsOfEvent(Long eventId, Integer from, Integer size);

    //Добавление пользователем нового комментария
    CommentDto addNewComment(Long commentatorId, Long eventId, NewCommentDto newCommentDto);

    //Обновлене(редактирование) комментария пользователем
    CommentDto updateComment(Long commentatorId, Long commentId, NewCommentDto newCommentDto);

    //Удаление пользователем своего комментария
    void deleteCommentByUser(Long commentatorId, Long commentId);

    //Удаление комментария администратором(модератором)
    void deleteCommentByAdmin(Long commentId);
}
