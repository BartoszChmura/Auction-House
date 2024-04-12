package com.auctionsystem.auctionhouse.repository;

import com.auctionsystem.auctionhouse.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
