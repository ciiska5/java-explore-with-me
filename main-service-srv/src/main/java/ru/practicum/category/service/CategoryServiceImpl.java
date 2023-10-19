package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.CategoryExistsException;
import ru.practicum.exception.CategoryInUseException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    //Получение категорий
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        log.info("Получение списка всех категорий");
        PageRequest pageRequest = PageRequest.of(from / size, size);

        log.info("Получен список всех категорий");
        return categoryRepository.findAll(pageRequest).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    //Получение информации о категории по её идентификатору
    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long catId) {
        log.info("Получение категории с id = {}", catId);
        Category category = checkCategoryExistence(catId);

        log.info("Получена категория с id = {}", catId);
        return CategoryMapper.toCategoryDto(category);
    }

    //Добавление новой категории
    @Override
    @Transactional
    public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
        log.info("Добавление новой категории");
        String newName = newCategoryDto.getName();

        checkCategoryUniqueness(newName);

        Category newCategory = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
        log.info("Добавлена новая категория с id = {}", newCategory.getId());

        return CategoryMapper.toCategoryDto(newCategory);
    }

    //Удаление категории
    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.info("Удаление категории с id = {}", catId);
        checkCategoryExistence(catId);

        if (eventRepository.existsByCategoryId(catId)) {
            throw new CategoryInUseException("Нельзя удалять категории с привязанными к ней событиями");
        }

        log.info("Удалена категории с id = {}", catId);
        categoryRepository.deleteById(catId);
    }

    //Изменеие категории
    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("Обновление категории с id = {}", catId);
        Category category = checkCategoryExistence(catId);

        if (!category.getName().equals(categoryDto.getName())) {
            checkCategoryUniqueness(categoryDto.getName());
        }

        category.setName(categoryDto.getName());

        log.info("Обновлена категории с id = {}", catId);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    //проверка существования категории
    private Category checkCategoryExistence(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + " не найдена"));
    }

    //проверка имени категории на уникальность
    private void checkCategoryUniqueness(String catName) {
        if (categoryRepository.existsByNameIgnoreCase(catName)) {
            throw new CategoryExistsException("Категория '" + catName + "' уже существует");
        }
    }
}
