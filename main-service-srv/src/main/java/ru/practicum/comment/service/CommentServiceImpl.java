package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.dto.ViewStats;
import ru.practicum.event.enums.states.EventState;
import ru.practicum.event.enums.status.RequestStatus;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.request.repository.RequestRepository;
import ru.practicum.exception.CommentValidationException;
import ru.practicum.exception.EventValidationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    /**
     * Для PUBLIC API
     */
    //Получение комментариев к событию
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsOfEvent(Long eventId, Integer from, Integer size) {
        log.info("Получение комментариев к событию с id = {}", eventId);
        Event event = checkEventExistence(eventId);

        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "created"));

        List<Comment> commentList = commentRepository.findAllByEventId(eventId, pageRequest);

        Long views = getViewStats(event);
        Long confirmedRequest = requestRepository.countRequestByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        log.info("УСПЕШНО получены комментарии к событию с id = {}", eventId);
        return commentList.stream()
                .map(comment -> CommentMapper.toCommentDto(comment, views, confirmedRequest))
                .collect(Collectors.toList());
    }

    /**
     * Для PRIVATE API
     */
    @Override
    @Transactional
    public CommentDto addNewComment(Long commentatorId, Long eventId, NewCommentDto newCommentDto) {
        log.info("Добавление нового комментария к событию с id = {} пользователем с id = {}", eventId, commentatorId);
        User commentator = checkUserExistence(commentatorId);
        Event event = checkEventExistence(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventValidationException("Событие с id = " + eventId + " не опубликован");
        }

        Comment newComment = commentRepository.save(CommentMapper.toComment(event, commentator, newCommentDto, timeToString(LocalDateTime.now())));

        log.info("УСПЕШНО добавлен новый комментарий к событию с id = {} пользователем с id = {}", eventId, commentatorId);

        Long views = getViewStats(event);
        Long confirmedRequest = requestRepository.countRequestByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        return CommentMapper.toCommentDto(newComment, views, confirmedRequest);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long commentatorId, Long commentId, NewCommentDto newCommentDto) {
        log.info("Обновление комментария с id = {} пользователем с id = {}", commentId, commentatorId);
        checkUserExistence(commentatorId);
        Comment comment = checkCommentExistence(commentId);

        if (!comment.getCommentator().getId().equals(commentatorId)) {
            throw new CommentValidationException("Пользователь может обновить только собственный комментарий");
        }

        comment.setText(newCommentDto.getText());
        log.info("УСПЕШНО обновлен комментарий с id = {} пользователем с id = {}", commentId, commentatorId);

        Long views = getViewStats(comment.getEvent());
        Long confirmedRequest = requestRepository.countRequestByEventIdAndStatus(
                comment.getEvent().getId(),
                RequestStatus.CONFIRMED
        );

        return CommentMapper.toCommentDto(comment, views, confirmedRequest);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long commentatorId, Long commentId) {
        log.info("Удаление комментария с id = {} пользователем с id = {}", commentId, commentatorId);
        checkUserExistence(commentatorId);
        Comment comment = checkCommentExistence(commentId);

        if (!comment.getCommentator().getId().equals(commentatorId)) {
            throw new CommentValidationException("Пользователь может удалить только собственный комментарий");
        }

        log.info("УСПЕШНО удален комментарий с id = {} пользователем с id = {}", commentId, commentatorId);
        commentRepository.deleteById(commentId);
    }

    /**
     * Для ADMIN API
     */

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        log.info("Удаление комментария с id = {}", commentId);
        checkCommentExistence(commentId);

        log.info("УСПЕШНО удален комментарий с id = {}", commentId);
        commentRepository.deleteById(commentId);
    }

    /**
     * Вспомогательные методы
     */
    //проверка существования события
    private Event checkEventExistence(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    //проверка существования пользователя
    private User checkUserExistence(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    //проверка существования комментария
    private Comment checkCommentExistence(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = " + commentId + " не найден"));
    }

    //конвертация времени из LocalDateTime в строку
    private String timeToString(LocalDateTime time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (time == null) {
            return null;
        }
        return time.format(dateTimeFormatter);
    }

    //получение статистики по просмотрам для одного события
    private Long getViewStats(Event event) {
        String uri = "/events/" + event.getId();
        List<String> uris = List.of(uri);

        List<ViewStats> viewStats = statsClient.getViewStats(
                timeToString(event.getCreatedOn()),
                timeToString(LocalDateTime.now().plusSeconds(1)),
                uris,
                true
        );

        if (viewStats.size() > 0) {
            return viewStats.get(0).getHits();
        }
        return 0L;
    }
}
