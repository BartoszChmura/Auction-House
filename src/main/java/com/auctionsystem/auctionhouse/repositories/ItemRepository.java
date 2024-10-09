package com.auctionsystem.auctionhouse.repositories;

import com.auctionsystem.auctionhouse.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}