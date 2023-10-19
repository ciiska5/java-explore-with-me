package ru.practicum.category.model;

import lombok.*;

import javax.persistence.*;

/**
 * Шаблон объекта Category для хранилища.
 * Категория события
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Идентификатор категории

    @Column(name = "name", nullable = false, unique = true)
    private String name;//Название категории
}
