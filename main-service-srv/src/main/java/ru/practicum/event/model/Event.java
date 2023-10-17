package ru.practicum.event.model;

import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.event.enums.states.EventState;
import ru.practicum.event.location.model.LocationDB;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Шаблон объекта Compilation для хранилища.
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Идентификатор

    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;//Краткое описание

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;//Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "confirmed_requests")
    private Long confirmedRequests;//Количество одобренных заявок на участие в данном событии

    @Column(name = "description", nullable = false, length = 7000)
    private String description;//Полное описание события

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;//Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "paid", nullable = false)
    private Boolean paid;//Нужно ли оплачивать участие

    @Column(name = "participant_limit", nullable = false)
    private Long participantLimit;//Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    @Column(name = "published_on")
    private LocalDateTime publishedOn;//Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;//Нужна ли пре-модерация заявок на участие

    @Column(nullable = false, length = 256)
    @Enumerated(EnumType.STRING)
    private EventState state;//Список состояний жизненного цикла события

    @Column(name = "title", nullable = false, length = 150)
    private String title;//Заголовок

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @OneToOne
    @JoinColumn(name = "location_id", nullable = false)
    private LocationDB location;
}
