package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.CategoryDto;
import com.auctionsystem.auctionhouse.entities.Category;
import com.auctionsystem.auctionhouse.mappers.CategoryMapper;
import com.auctionsystem.auctionhouse.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testSaveCategory() {
        // Given
        CategoryDto categoryDto = createCategoryDto(1L);

        Category category = createCategory(1L);

        when(categoryMapper.toEntity(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        //When
        CategoryDto savedCategoryDto = categoryService.saveCategory(categoryDto);

        //Then
        assertNotNull(savedCategoryDto);
        assertEquals(categoryDto.getId(), savedCategoryDto.getId());
        assertEquals(categoryDto.getCategoryName(), savedCategoryDto.getCategoryName());
        verify(categoryMapper, times(1)).toEntity(categoryDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    public void testGetCategoryById() {
        // Given
        Category category = createCategory(1L);

        CategoryDto categoryDto = createCategoryDto(1L);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        // When
        Optional<CategoryDto> result = categoryService.getCategoryById(category.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(categoryDto, result.get());
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    public void testGetAllCategories() {
        // Given
        Category category1 = createCategory(1L);
        Category category2 = createCategory(2L);
        List<Category> categoryList = Arrays.asList(category1, category2);

        CategoryDto categoryDto1 = createCategoryDto(1L);
        CategoryDto categoryDto2 = createCategoryDto(2L);
        List<CategoryDto> categoryDtoList = Arrays.asList(categoryDto1, categoryDto2);

        when(categoryRepository.findAll()).thenReturn(categoryList);
        when(categoryMapper.toDto(category1)).thenReturn(categoryDto1);
        when(categoryMapper.toDto(category2)).thenReturn(categoryDto2);

        // When
        List<CategoryDto> result = categoryService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(categoryDtoList));
        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(1)).toDto(category1);
        verify(categoryMapper, times(1)).toDto(category2);
    }

    @Test
    public void testUpdateCategory() {
        // Given
        Category existingCategory = createCategory(1L);
        CategoryDto categoryDto = createCategoryDto(1L);
        categoryDto.setCategoryName("updatedCategory");

        when(categoryRepository.findById(categoryDto.getId())).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        when(categoryMapper.toDto(existingCategory)).thenReturn(categoryDto);

        // When
        CategoryDto result = categoryService.updateCategory(categoryDto);

        // Then
        assertNotNull(result);
        assertEquals(categoryDto.getId(), result.getId());
        assertEquals(categoryDto.getCategoryName(), result.getCategoryName());
        verify(categoryRepository, times(1)).findById(categoryDto.getId());
        verify(categoryRepository, times(1)).save(existingCategory);
        verify(categoryMapper, times(1)).toDto(existingCategory);
    }

    @Test
    public void testDeleteCategory() {
        // Given
        CategoryDto categoryDto = createCategoryDto(1L);
        doNothing().when(categoryRepository).deleteById(categoryDto.getId());

        // When
        categoryService.deleteCategory(categoryDto.getId());

        // Then
        verify(categoryRepository, times(1)).deleteById(categoryDto.getId());
    }

    public CategoryDto createCategoryDto(Long id) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(id);
        categoryDto.setCategoryName("testCategory");
        return categoryDto;
    }

    public Category createCategory(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setCategoryName("testCategory");
        return category;
    }
}
