package ru.practicum.event.location.model;

import lombok.*;

import javax.persistence.*;

/**
 * Шаблон объекта Location для хранилища.
 * Широта и долгота места проведения события
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Идентификатор

    @Column(name = "lat", nullable = false)
    private Float lat;//Широта

    @Column(name = "lon", nullable = false)
    private Float lon;//Долгота
}
