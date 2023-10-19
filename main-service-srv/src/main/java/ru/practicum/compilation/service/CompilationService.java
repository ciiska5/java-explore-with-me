package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    //Получение подбророк событий
    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);


    //Получение подброрки событий по его id
    CompilationDto getCompilationById(Long compId);

    //Добавление новой подборки (подборка может не содержать событий)
    CompilationDto addNewCompilation(NewCompilationDto newCompilationDto);

    //Удаление подборки
    void deleteCompilation(Long compId);

    //Обновить информацию о подборке
    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
