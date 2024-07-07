package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.CategoryDto;
import com.auctionsystem.auctionhouse.entities.Category;
import com.auctionsystem.auctionhouse.mappers.CategoryMapper;
import com.auctionsystem.auctionhouse.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        log.info("Zapisywanie kategorii o id {}", categoryDto.getId());
        Category category = categoryMapper.toEntity(categoryDto);
        if (getCategoryByCategoryName(category.getCategoryName()).isPresent()) {
            throw new IllegalArgumentException(String.format("Kategoria %s już istnieje", category.getCategoryName()));
        }
        Category savedCategory = categoryRepository.save(category);
        log.info("Zapisano kategorię o id {}", savedCategory.getId());
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional
    public Optional<CategoryDto> getCategoryById(Long id) {
        log.info("Pobieranie kategorii o id: {}", id);
        Optional<CategoryDto> result = categoryRepository.findById(id)
                .map(categoryMapper::toDto);
        log.info("Pobrano kategorię o id: {}", id);
        return result;
    }

    @Transactional
    public Optional<Category> getCategoryEntityById(Long id) {
        log.info("Pobieranie encji kategorii o id: {}", id);
        Optional<Category> result = categoryRepository.findById(id);
        log.info("Pobrano encję kategorii o id: {}", id);
        return result;
    }

    @Transactional
    public Optional<CategoryDto> getCategoryByCategoryName(String categoryName) {
        log.info("Pobieranie kategorii o nazwie: {}", categoryName);
        Optional<CategoryDto> result = Optional.ofNullable(categoryRepository.getCategoryByCategoryName(categoryName))
                .map(categoryMapper::toDto);
        log.info("Pobrano kategorię o nazwie: {}", categoryName);
        return result;
    }

    @Transactional
    public List<CategoryDto> getAllCategories() {
        log.info("Pobieranie wszystkich kategorii");
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> result = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        log.info("Pobrano wszystkie kategorie");
        return result;
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        log.info("Aktualizacja kategorii: {}", categoryDto);
        Category existingCategory = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono kategorii z takim ID"));

        if (categoryDto.getCategoryName() != null && !categoryDto.getCategoryName().isEmpty()) {
            existingCategory.setCategoryName(categoryDto.getCategoryName());
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        log.info("Kategoria zaktualizowana pomyślnie: {}", updatedCategory);
        return categoryMapper.toDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Usuwanie kategorii o id: {}", id);
        categoryRepository.deleteById(id);
        log.info("Kategoria o id {} została pomyślnie usunięta", id);
    }
}
