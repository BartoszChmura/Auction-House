package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.BidDto;
import com.auctionsystem.auctionhouse.dtos.ItemDto;
import com.auctionsystem.auctionhouse.entities.Bid;
import com.auctionsystem.auctionhouse.entities.User;
import com.auctionsystem.auctionhouse.mappers.BidMapper;
import com.auctionsystem.auctionhouse.repositories.BidRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BidService {

    private final BidRepository bidRepository;
    private final BidMapper bidMapper;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BidService(BidRepository bidRepository, BidMapper bidMapper, @Lazy ItemService itemService, UserService userService) {
        this.bidRepository = bidRepository;
        this.bidMapper = bidMapper;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Transactional
    public BidDto saveBid(BidDto bidDto) {
        log.info("Saving bid with id {}", bidDto.getId());
        Optional<ItemDto> existingItem = itemService.getItemById(bidDto.getItemId());
        if (existingItem.isEmpty()) {
            throw new IllegalArgumentException("Item with id " + bidDto.getItemId() + " does not exist");
        }
        if (!existingItem.get().getStatus().equals("active")) {
            throw new IllegalArgumentException("Auction must be active");
        }
        if (bidDto.getBidAmount() == null) {
            throw new IllegalArgumentException("Amount cannot be empty");
        }
        if (bidDto.getBidAmount() <= 0 || bidDto.getBidAmount() <= existingItem.get().getCurrentPrice()) {
            throw new IllegalArgumentException("Amount must be higher than the current item price");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User bidder = userService.getUserEntityByUsername(username).orElseThrow(() -> new IllegalArgumentException("User does not exist"));
        Bid bid = bidMapper.toEntity(bidDto);
        bid.setBidder(bidder);
        Bid savedBid = bidRepository.save(bid);

        existingItem.get().setCurrentPrice(bidDto.getBidAmount());
        itemService.updateCurrentPrice(existingItem.get());
        log.info("Saved bid with id {}", bidDto.getId());

        return bidMapper.toDto(savedBid);
    }

    @Transactional
    public Optional<BidDto> getBidById(Long id) {
        log.info("Retrieving bid with id: {}", id);
        Optional<BidDto> result = bidRepository.findById(id)
                .map(bidMapper::toDto);
        log.info("Retrieved bid with id: {}", id);

        return result;
    }

    @Transactional
    public Optional<Bid> getBidEntityById(Long id) {
        log.info("Retrieving bid entity with id: {}", id);
        Optional<Bid> result = bidRepository.findById(id);
        log.info("Retrieved bid entity with id: {}", id);

        return result;
    }

    @Transactional
    public List<BidDto> getAllBids() {
        log.info("Retrieving all bids");
        List<Bid> bids = bidRepository.findAll();
        List<BidDto> result = bids.stream()
                .map(bidMapper::toDto)
                .collect(Collectors.toList());
        log.info("Retrieved all bids");

        return result;
    }

    @Transactional
    public List<BidDto> getBidsByItemId(Long itemId) {
        log.info("Retrieving bids for item with id: {}", itemId);
        List<Bid> bids = bidRepository.getBidsByItemId(itemId);
        List<BidDto> result = bids.stream()
                .map(bidMapper::toDto)
                .collect(Collectors.toList());
        log.info("Retrieved bids for item with id: {}", itemId);

        return result;
    }

    @Transactional
    public Optional<BidDto> getWinnerBidByItemId(Long itemId) {
        log.info("Retrieving winning bid for item with id: {}", itemId);
        List<BidDto> bids = getBidsByItemId(itemId);
        if (bids.isEmpty()) {
            return Optional.empty();
        } else {
            Optional<BidDto> result = Optional.ofNullable(bids.getLast());
            log.info("Retrieved winning bid for item with id: {}", itemId);
            return result;
        }
    }

    @Transactional
    public void deleteBid(Long id) {
        log.info("Deleting bid with id: {}", id);
        bidRepository.deleteById(id);
        log.info("Bid with id {} has been successfully deleted", id);
    }

    public boolean isUserAuthorizedToUpdateBid(Long id) {
        log.info("Checking if user is authorized to update bid with id: {}", id);
        Optional<BidDto> bidDto = getBidById(id);
        if (bidDto.isEmpty()) {
            throw new IllegalArgumentException("Bid with id " + id + " does not exist");
        }

        Long bidderId = bidDto.get().getBidderId();
        boolean isAuthorized = userService.isUserAuthorizedToUpdate(bidderId);
        log.info("User {} is {} to update bid with id: {}", bidderId, isAuthorized ? "authorized" : "not authorized", id);

        return isAuthorized;
    }
}