package ru.practicum.server.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


/**
 * Шаблон объекта EndpointHit для хранилища.
 * Используется для сохранения информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
 */


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hits")
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Идентификатор записи

    @Column(nullable = false, length = 256)
    private String app;//Идентификатор сервиса для которого записывается информация

    @Column(length = 256)
    private String uri;//URI для которого был осуществлен запрос

    @Column(length = 256)
    private String ip;//IP-адрес пользователя, осуществившего запрос

    @Column(nullable = false)
    private LocalDateTime timestamp;//Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
}
