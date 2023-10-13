package ru.practicum.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //получение всех пользователей по запрашиваемому списку id
    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    //проверка существования email
    Boolean existsByEmail(String email);
}
