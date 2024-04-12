package com.auctionsystem.auctionhouse.service;

import com.auctionsystem.auctionhouse.dto.BidDto;
import com.auctionsystem.auctionhouse.entity.Bid;
import com.auctionsystem.auctionhouse.mapper.BidMapper;
import com.auctionsystem.auctionhouse.repository.BidRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final BidMapper bidMapper;

    public BidService(BidRepository bidRepository, BidMapper bidMapper) {
        this.bidRepository = bidRepository;
        this.bidMapper = bidMapper;
    }

    @Transactional
    public BidDto saveBid(Bid bid) {
        Bid savedBid = bidRepository.save(bid);
        return bidMapper.toDto(savedBid);
    }

    @Transactional
    public Optional<BidDto> getBidById(Long id) {
        return bidRepository.findById(id)
                .map(bidMapper::toDto);
    }

    @Transactional
    public List<BidDto> getAllBids() {
        List<Bid> bids = bidRepository.findAll();
        return bids.stream()
                .map(bidMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BidDto updateBid(Bid bid) {
        Bid updatedBid = bidRepository.save(bid);
        return bidMapper.toDto(updatedBid);
    }

    @Transactional
    public void deleteBid(Long id) {
        bidRepository.deleteById(id);
    }
}
