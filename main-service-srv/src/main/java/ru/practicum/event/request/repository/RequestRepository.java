package ru.practicum.event.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.enums.status.RequestStatus;
import ru.practicum.event.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    //получентие запроса по пользователю и событию
    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    //получение запросов пользователя
    List<Request> findAllByRequesterId(Long requesterId);

    //проверка существования запроса текущим пользователем на текущее событие
    Boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    //получение запросов к событию
    List<Request> findAllByEventId(Long eventId);

    //получену всех запросов по списку id и статусу
    List<Request> findAllByIdInAndStatus(List<Long> ids, RequestStatus status);

    //обновление данных запроса
    @Modifying
    @Query("UPDATE Request AS r " +
            "SET r.status = :newStatus " +
            "WHERE r.id IN :ids AND r.status = :oldStatus")
    void updateRequests(List<Long> ids, RequestStatus newStatus, RequestStatus oldStatus);
}
