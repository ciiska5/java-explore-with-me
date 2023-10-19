package ru.practicum.event.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.location.model.LocationDB;

@Repository
public interface LocationRepository extends JpaRepository<LocationDB, Long> {

}
