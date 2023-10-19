package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

/**
 * API для работы с подборками событий
 */

@RestController
@RequestMapping(path = "admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    //Добавление новой подборки (подборка может не содержать событий)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addNewCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.addNewCompilation(newCompilationDto);
    }

    //Удаление подборки
    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive Long compId) {
        compilationService.deleteCompilation(compId);
    }

    //Обновить информацию о подборке
    @PatchMapping("/{compId}")
    public CompilationDto update(@RequestBody @Valid UpdateCompilationRequest updateCompilationRequest,
                                 @PathVariable @Positive Long compId) {
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }
}
