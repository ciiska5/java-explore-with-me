package ru.practicum.event.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.enums.status.RequestStatus;
import ru.practicum.event.request.model.ConfirmedRequestsCount;
import ru.practicum.event.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    //получентие запроса по пользователю и событию
    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    //получение запросов пользователя
    List<Request> findAllByRequesterId(Long requesterId);

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

    //получение списка объектов с данными по кол-ву подтвержденных заявок для списка ивентов
    long countRequestByEventIdAndStatus(Long eventId, RequestStatus status);

    //получение списка объектов с данными по кол-ву подтвержденных заявок для списка ивентов
    @Query("SELECT new ru.practicum.event.request.model.ConfirmedRequestsCount(r.event.id, COUNT(*)) " +
            "FROM Request AS r " +
            "WHERE r.event.id IN :eventIds AND r.status = :status " +
            "GROUP BY r.event.id")
    List<ConfirmedRequestsCount> getConfirmedRequestsCountList(List<Long> eventIds, RequestStatus status);
}
