package ru.practicum.compilation.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    //получение закрепленных/незакрепленных подборок
    List<Compilation> findAllByPinned(Boolean pinned, PageRequest pageRequest);
}
