package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    //Получение категорий
    List<CategoryDto> getAllCategories(Integer from, Integer size);

    //Получение информации о категории по её идентификатору
    CategoryDto getCategoryById(Long catId);

    //Добавление новой категории
    CategoryDto addNewCategory(NewCategoryDto newCategoryDto);

    //Удаление категории
    void deleteCategory(Long catId);

    //Изменеие категории
    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);
}
