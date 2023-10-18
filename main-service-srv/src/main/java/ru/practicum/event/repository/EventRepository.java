package ru.practicum.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.enums.states.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.parameters.AdminParameters;
import ru.practicum.event.repository.parameters.PublicParameters;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    //получение всех событий, добавленных текущим пользователем
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    //получение события, добавленном текущим пользователем
    Event findByInitiatorIdAndId(Long userId, Long eventId);

    //получение списка событий, у которых не исчерпан лимит запросов на участие
    @Query("SELECT e FROM Event AS e " +
            "WHERE " +
            "e.state = 'PUBLISHED' AND " +
            "e.participantLimit = 0 AND " +
            "(LOWER(e.annotation) LIKE %:#{#parameters.text}% OR " +
            "LOWER(e.description) LIKE %:#{#parameters.text}% OR " +
            ":#{#parameters.text} IS NULL) AND " +
            "(e.category.id IN :#{#parameters.categories} OR :#{#parameters.categories} IS NULL) AND " +
            "(e.paid = :#{#parameters.paid} or :#{#parameters.paid} IS NULL) AND " +
            "e.eventDate BETWEEN :rangeStart AND :rangeEnd")
    List<Event> searchAvailableEventsByPublic(@Param("parameters") PublicParameters parameters,
                                              @Param("rangeStart") LocalDateTime rangeStart,
                                              @Param("rangeEnd") LocalDateTime rangeEnd,
                                              PageRequest pageRequest);

    //получение списка всех опубликованных событий
    @Query("SELECT e FROM Event AS e " +
            "WHERE " +
            "e.state = 'PUBLISHED' AND " +
            "(LOWER(e.annotation) LIKE %:#{#parameters.text}% OR " +
            "LOWER(e.description) LIKE %:#{#parameters.text}% OR " +
            ":#{#parameters.text} IS NULL) AND " +
            "(e.category.id IN :#{#parameters.categories} OR :#{#parameters.categories} IS NULL) AND " +
            "(e.paid = :#{#parameters.paid} OR :#{#parameters.paid} IS NULL) AND " +
            "e.eventDate BETWEEN :rangeStart AND :rangeEnd")
    List<Event> getAllPublishedEvents(@Param("parameters") PublicParameters parameters,
                                      @Param("rangeStart") LocalDateTime rangeStart,
                                      @Param("rangeEnd") LocalDateTime rangeEnd,
                                PageRequest pageRequest);

    //получение списка событий по списку id
    List<Event> findAllByIdIn(List<Long> eventIds);

    //проверка на существование события с заданной категорией
    Boolean existsByCategoryId(Long catId);

    //получение списка событий администратором
    @Query("SELECT e FROM Event AS e " +
            "WHERE " +
            "(e.initiator.id IN :#{#parameters.users} OR :#{#parameters.users} IS NULL) AND " +
            "(e.state IN :states OR :states IS NULL) AND " +
            "(e.category.id IN :#{#parameters.categories} OR :#{#parameters.categories} IS NULL) AND " +
            "e.eventDate BETWEEN :rangeStart AND :rangeEnd")
    List<Event> getEventsByAdmin(@Param("parameters") AdminParameters parameters,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 @Param("states") List<EventState> states,
                                 PageRequest pageRequest);
}
