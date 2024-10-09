package com.auctionsystem.auctionhouse.mappers;

import com.auctionsystem.auctionhouse.dtos.CategoryDto;
import com.auctionsystem.auctionhouse.entities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setCategoryName(category.getCategoryName());

        return categoryDto;
    }

    public Category toEntity(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setCategoryName(categoryDto.getCategoryName());

        return category;
    }
}