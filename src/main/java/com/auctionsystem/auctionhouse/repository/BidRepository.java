package com.auctionsystem.auctionhouse.repository;

import com.auctionsystem.auctionhouse.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
}
