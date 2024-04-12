package com.auctionsystem.auctionhouse.service;

import com.auctionsystem.auctionhouse.entity.Category;
import com.auctionsystem.auctionhouse.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    @Transactional
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }
    @Transactional
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    @Transactional
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    @Transactional
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
