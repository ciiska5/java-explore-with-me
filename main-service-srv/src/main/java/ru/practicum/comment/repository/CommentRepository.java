package ru.practicum.comment.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //получение всех комментариев к событию
    List<Comment> findAllByEventId(Long eventId, PageRequest pageRequest);
}
