package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

/**
 * Публичный API для работы с комментариями
 **/

@RestController
@RequestMapping(path = "/comments/{eventId}")
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;

    //Получение комментариев к событию для незарегестрированных пользователей
    @GetMapping
    public List<CommentDto> getCommentsOfEventForPublic(@PathVariable Long eventId,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentsOfEvent(eventId, from, size);
    }
}
