package com.auctionsystem.auctionhouse.repository;

import com.auctionsystem.auctionhouse.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> getBidsByItemId(Long itemId);
}
