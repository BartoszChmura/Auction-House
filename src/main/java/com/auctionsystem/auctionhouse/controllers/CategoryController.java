package com.auctionsystem.auctionhouse.controllers;

import com.auctionsystem.auctionhouse.dtos.CategoryDto;

import com.auctionsystem.auctionhouse.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody CategoryDto categoryDto) {
        try {
            CategoryDto savedCategory = categoryService.saveCategory(categoryDto);
            return ResponseEntity.ok(savedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        Optional<CategoryDto> categoryDto = categoryService.getCategoryById(id);
        if (categoryDto.isPresent()) {
            return ResponseEntity.ok(categoryDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item with id " + id + " does not exist");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        Optional<CategoryDto> existingCategory = categoryService.getCategoryById(id);
        if (existingCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with id " + id + " does not exist");
        }
        categoryDto.setId(id);
        CategoryDto updatedCategory = categoryService.updateCategory(categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        Optional<CategoryDto> existingCategory = categoryService.getCategoryById(id);
        if (existingCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with id " + id + " does not exist");
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category with id " + id + " has been deleted");
    }

}
