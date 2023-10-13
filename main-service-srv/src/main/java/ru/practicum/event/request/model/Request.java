package ru.practicum.event.request.model;

import lombok.*;
import ru.practicum.event.enums.status.RequestStatus;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Шаблон объекта Request для хранилища.
 * Заявка на участие в событии
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//Идентификатор заявки

    @Column(name = "created", nullable = false)
    private LocalDateTime created;//Дата и время создания заявки

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;//Статус заявки

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;//Событие, на которое пользователь подает заявку

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;//Пользователь, отправивший заявку
}
