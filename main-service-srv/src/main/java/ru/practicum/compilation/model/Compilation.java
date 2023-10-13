package ru.practicum.compilation.model;

import lombok.*;
import ru.practicum.event.model.Event;

import javax.persistence.*;
import java.util.List;

/**
 * Шаблон объекта Compilation для хранилища.
 * Подборка событий.
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Идентификатор

    @Column(name = "pinned", nullable = false)
    private Boolean pinned;//Закреплена ли подборка на главной странице сайта

    @Column(name = "title", nullable = false, length = 50, unique = true)
    private String title;//Заголовок подборки

    @OneToMany
    @JoinTable(name = "compilation_event",
            joinColumns = @JoinColumn(name = "compilation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id")
    )
    private List<Event> events;//Список событий входящих в подборку
}
