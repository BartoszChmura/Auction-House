package com.auctionsystem.auctionhouse.controller;

import com.auctionsystem.auctionhouse.dto.CategoryDto;

import com.auctionsystem.auctionhouse.service.CategoryService;
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Przedmiot o id " + id + " nie istnieje");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        Optional<CategoryDto> existingCategory = categoryService.getCategoryById(id);
        if (existingCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kategoria o id " + id + " nie istnieje");
        }
        categoryDto.setId(id);
        CategoryDto updatedCategory = categoryService.updateCategory(categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        Optional<CategoryDto> existingCategory = categoryService.getCategoryById(id);
        if (existingCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kategoria o id " + id + " nie istnieje");
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Kategoria o id " + id + " została usunięta");
    }

}
