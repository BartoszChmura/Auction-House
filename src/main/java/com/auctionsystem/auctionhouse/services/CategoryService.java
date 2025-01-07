package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.CategoryDto;
import com.auctionsystem.auctionhouse.entities.Category;
import com.auctionsystem.auctionhouse.mappers.CategoryMapper;
import com.auctionsystem.auctionhouse.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        log.info("Saving category with id {}", categoryDto.getId());
        Category category = categoryMapper.toEntity(categoryDto);
        if (getCategoryByCategoryName(category.getCategoryName()).isPresent()) {
            throw new IllegalArgumentException(String.format("Category %s already exists", category.getCategoryName()));
        }
        Category savedCategory = categoryRepository.save(category);
        log.info("Saved category with id {}", savedCategory.getId());

        return categoryMapper.toDto(savedCategory);
    }

    @Transactional
    public Optional<CategoryDto> getCategoryById(Long id) {
        log.info("Retrieving category with id: {}", id);
        Optional<CategoryDto> result = categoryRepository.findById(id)
                .map(categoryMapper::toDto);
        log.info("Retrieved category with id: {}", id);

        return result;
    }

    @Transactional
    public Optional<Category> getCategoryEntityById(Long id) {
        log.info("Retrieving category entity with id: {}", id);
        Optional<Category> result = categoryRepository.findById(id);
        log.info("Retrieved category entity with id: {}", id);

        return result;
    }

    @Transactional
    public Optional<CategoryDto> getCategoryByCategoryName(String categoryName) {
        log.info("Retrieving category entity with name: {}", categoryName);
        Optional<CategoryDto> result = Optional.ofNullable(categoryRepository.getCategoryByCategoryName(categoryName))
                .map(categoryMapper::toDto);
        log.info("Retrieved category entity with name: {}", categoryName);

        return result;
    }

    @Transactional
    public List<CategoryDto> getAllCategories() {
        log.info("Retrieving all categories");
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> result = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        log.info("Retrieved all categories");

        return result;
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        log.info("Updating category: {}", categoryDto);
        Category existingCategory = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category with such ID not found"));

        if (categoryDto.getCategoryName() != null && !categoryDto.getCategoryName().isEmpty()) {
            existingCategory.setCategoryName(categoryDto.getCategoryName());
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        log.info("Category updated successfully: {}", updatedCategory);

        return categoryMapper.toDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);
        categoryRepository.deleteById(id);
        log.info("Category with id {} has been successfully deleted", id);
    }
}