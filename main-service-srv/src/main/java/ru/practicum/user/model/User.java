package ru.practicum.user.model;

import lombok.*;

import javax.persistence.*;

/**
 * Шаблон объекта User для хранилища.
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//Идентификатор

    @Column(name = "email", nullable = false, length = 512, unique = true)
    private String email;//Почтовый адрес

    @Column(name = "name", nullable = false, length = 256)
    private String name;//Имя
}
