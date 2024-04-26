package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.CategoryDto;
import com.auctionsystem.auctionhouse.entities.Category;
import com.auctionsystem.auctionhouse.mappers.CategoryMapper;
import com.auctionsystem.auctionhouse.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        if(getCategoryByCategoryName(category.getCategoryName()).isPresent())
            throw new IllegalArgumentException(String.format("Kategoria %s już istnieje", category.getCategoryName()));
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional
    public Optional<CategoryDto> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto);
    }

    @Transactional
    public Optional<Category> getCategoryEntityById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Optional<CategoryDto> getCategoryByCategoryName(String categoryName) {
        return Optional.ofNullable(categoryRepository.getCategoryByCategoryName(categoryName))
                .map(categoryMapper::toDto);
    }

    @Transactional
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono kategorii z takim ID"));

        if (categoryDto.getCategoryName() != null || !categoryDto.getCategoryName().isEmpty()) {
            existingCategory.setCategoryName(categoryDto.getCategoryName());
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toDto(updatedCategory);

    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
