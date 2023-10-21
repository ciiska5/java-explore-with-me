package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.format.DateTimeFormatter;

import static ru.practicum.event.mapper.EventMapper.toEventShortDto;
import static ru.practicum.user.mapper.UserMapper.toUserShortDto;

public class CommentMapper {
    public static Comment toComment(Event event, User user, NewCommentDto newCommentDto) {
        Comment comment = new Comment();

        comment.setText(newCommentDto.getText());
        comment.setCommentator(user);
        comment.setEvent(event);

        return comment;
    }

    public static CommentDto toCommentDto(Comment comment, Long eventViews, Long eventConfirmedRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        CommentDto commentDto = new CommentDto();

        Event event = comment.getEvent();

        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated().format(formatter));
        commentDto.setCommentator(toUserShortDto(comment.getCommentator()));
        commentDto.setEvent(toEventShortDto(event, eventViews, eventConfirmedRequest));
        commentDto.setUpdatedTime(comment.getUpdatedTime().format(formatter));

        return commentDto;
    }
}
