package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    //получение списка уникальных по ip ViewStats без URI  в диапазоне start и end с сортировкой кол-ва просмотров по убыванию
    @Query("SELECT new ru.practicum.dto.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(DISTINCT eh.ip) DESC")
    List<ViewStats> findUniqueIpAndNonUriViewStats(LocalDateTime start, LocalDateTime end);

    //получение списка НЕуникальных по ip ViewStats без URI в диапазоне start и end с сортировкой кол-ва просмотров по убыванию
    @Query("SELECT new ru.practicum.dto.ViewStats(eh.app, eh.uri, COUNT(eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.ip) DESC")
    List<ViewStats> findNonUniqueIpAndNonUriViewStats(LocalDateTime start, LocalDateTime end);

    //получение списка уникальных по ip ViewStats с URI в диапазоне start и end с сортировкой кол-ва просмотров по убыванию
    @Query("SELECT new ru.practicum.dto.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN ?1 AND ?2 AND eh.uri IN ?3 " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(DISTINCT eh.ip) DESC")
    List<ViewStats> findUniqueIpAndUriContainingViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    //получение списка НЕуникальных по ip ViewStats c URI в диапазоне start и end с сортировкой кол-ва просмотров по убыванию
    @Query("SELECT new ru.practicum.dto.ViewStats(eh.app, eh.uri, COUNT(eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN ?1 AND ?2 AND eh.uri IN ?3 " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.ip) DESC")
    List<ViewStats> findNonUniqueIpAndContainingViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
