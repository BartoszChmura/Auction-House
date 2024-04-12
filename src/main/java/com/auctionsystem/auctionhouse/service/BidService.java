package com.auctionsystem.auctionhouse.service;

import com.auctionsystem.auctionhouse.entity.Bid;
import com.auctionsystem.auctionhouse.repository.BidRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BidService {

        private final BidRepository bidRepository;

        public BidService(BidRepository bidRepository) {
            this.bidRepository = bidRepository;
        }
        @Transactional
        public void saveBid(Bid bid) {
            bidRepository.save(bid);
        }
        @Transactional
        public Optional<Bid> getBidById(Long id) {
            return bidRepository.findById(id);
        }
        @Transactional
        public List<Bid> getAllBids() {
            return bidRepository.findAll();
        }
        @Transactional
        public Bid updateBid(Bid bid) {
            return bidRepository.save(bid);
        }
        @Transactional
        public void deleteBid(Long id) {
            bidRepository.deleteById(id);
        }
}
