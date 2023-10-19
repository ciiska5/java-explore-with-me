package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.event.mapper.EventMapper.toEventShortDto;
import static ru.practicum.user.mapper.UserMapper.toUserShortDto;

public class CommentMapper {
    public static Comment toComment(Event event, User user, NewCommentDto newCommentDto, String time) {
        Comment comment = new Comment();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        comment.setText(newCommentDto.getText());
        comment.setCreated(LocalDateTime.parse(time, formatter));
        comment.setCommentator(user);
        comment.setEvent(event);

        return comment;
    }

    public static CommentDto toCommentDto(Comment comment, Long eventViews, Long eventConfirmedRequest) {
        CommentDto commentDto = new CommentDto();

        Event event = comment.getEvent();

        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated().toString());
        commentDto.setCommentator(toUserShortDto(comment.getCommentator()));
        commentDto.setEvent(toEventShortDto(event, eventViews, eventConfirmedRequest));

        return commentDto;
    }
}
