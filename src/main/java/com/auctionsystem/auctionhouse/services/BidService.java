package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.BidDto;
import com.auctionsystem.auctionhouse.dtos.ItemDto;
import com.auctionsystem.auctionhouse.entities.Bid;
import com.auctionsystem.auctionhouse.entities.User;
import com.auctionsystem.auctionhouse.mappers.BidMapper;
import com.auctionsystem.auctionhouse.mappers.ItemMapper;
import com.auctionsystem.auctionhouse.repositories.BidRepository;
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
public class BidService {

    private final BidRepository bidRepository;
    private final BidMapper bidMapper;
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Autowired
    public BidService(BidRepository bidRepository, BidMapper bidMapper, @Lazy ItemService itemService, UserService userService, ItemMapper itemMapper) {
        this.bidRepository = bidRepository;
        this.bidMapper = bidMapper;
        this.itemService = itemService;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    @Transactional
    public BidDto saveBid(BidDto bidDto) {
        Optional<ItemDto> existingItem = itemService.getItemById(bidDto.getItemId());
        if (existingItem.isEmpty()) {
            throw new IllegalArgumentException("Przedmiot o id " + bidDto.getItemId() + " nie istnieje");
        }
        if (!existingItem.get().getStatus().equals("aktywna")) {
            throw new IllegalArgumentException("Licytacja musi być aktywna");
        }
        if (bidDto.getBidAmount() == null) {
            throw new IllegalArgumentException("Kwota nie może być pusta");
        }
        if (bidDto.getBidAmount() <= 0 || bidDto.getBidAmount() <= existingItem.get().getCurrentPrice()) {
            throw new IllegalArgumentException("Kwota musi być wyższa niż aktualna cena przedmiotu");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User bidder = userService.getUserEntityByUsername(username).orElseThrow(() -> new IllegalArgumentException("Użytkownik nie istnieje"));
        Bid bid = bidMapper.toEntity(bidDto);
        bid.setBidder(bidder);
        Bid savedBid = bidRepository.save(bid);

        existingItem.get().setCurrentPrice(bidDto.getBidAmount());
        itemService.updateCurrentPrice(existingItem.get());


        return bidMapper.toDto(savedBid);

    }

    @Transactional
    public Optional<BidDto> getBidById(Long id) {
        return bidRepository.findById(id)
                .map(bidMapper::toDto);
    }

    @Transactional
    public Optional<Bid> getBidEntityById(Long id) {
        return bidRepository.findById(id);
    }

    @Transactional
    public List<BidDto> getAllBids() {
        List<Bid> bids = bidRepository.findAll();
        return bids.stream()
                .map(bidMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BidDto> getBidsByItemId(Long itemId) {
        List<Bid> bids = bidRepository.getBidsByItemId(itemId);
        return bids.stream()
                .map(bidMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<BidDto> getWinnerBidByItemId(Long itemId) {
        List<BidDto> bids = getBidsByItemId(itemId);
        if (bids.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(bids.getLast());
        }
    }

    @Transactional
    public void deleteBid(Long id) {
        bidRepository.deleteById(id);
    }

    public boolean isUserAuthorizedToUpdateBid(Long id) {
        Optional<BidDto> bidDto = getBidById(id);
        if (bidDto.isEmpty()) {
            throw new IllegalArgumentException("Bid o id " + id + " nie istnieje");
        }

        Long bidderId = bidDto.get().getBidderId();

        return userService.isUserAuthorizedToUpdate(bidderId);

    }
}
