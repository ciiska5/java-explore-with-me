package ru.practicum.comment.model;

import lombok.*;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Шаблон объекта Comment для хранилища.
 * Комментарии к событиям.
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Идентификатор комментария

    @Column(name = "text", nullable = false, length = 1500)
    private String text; //Содержимое комментария пользователя

    @Column(name = "created", nullable = false)
    private LocalDateTime created;//Дата и время создания комментария (в формате "yyyy-MM-dd HH:mm:ss")

    @ManyToOne
    @JoinColumn(name = "commentator_id", nullable = false)
    private User commentator;//Пользователь, оставивший комментарий

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; //Событие, к которому пользователь оставил комментарий
}
