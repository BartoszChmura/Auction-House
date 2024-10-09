package com.auctionsystem.auctionhouse.repositories;

import com.auctionsystem.auctionhouse.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category getCategoryByCategoryName(String categoryName);
}