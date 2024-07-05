package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.BidDto;
import com.auctionsystem.auctionhouse.dtos.ItemDto;
import com.auctionsystem.auctionhouse.entities.Bid;
import com.auctionsystem.auctionhouse.entities.User;
import com.auctionsystem.auctionhouse.mappers.BidMapper;
import com.auctionsystem.auctionhouse.mappers.ItemMapper;
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
        log.info("Zapisywanie oferty");
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
        log.info("Zapisano ofertę");

        return bidMapper.toDto(savedBid);

    }

    @Transactional
    public Optional<BidDto> getBidById(Long id) {
        log.info("Pobieranie oferty o id: {}", id);
        Optional<BidDto> result = bidRepository.findById(id)
                .map(bidMapper::toDto);
        log.info("Pobrano ofertę o id: {}", id);
        return result;
    }

    @Transactional
    public Optional<Bid> getBidEntityById(Long id) {
        log.info("Pobieranie encji oferty o id: {}", id);
        Optional<Bid> result = bidRepository.findById(id);
        log.info("Pobrano encję oferty o id: {}", id);
        return result;
    }

    @Transactional
    public List<BidDto> getAllBids() {
        log.info("Pobieranie wszystkich ofert");
        List<Bid> bids = bidRepository.findAll();
        List<BidDto> result = bids.stream()
                .map(bidMapper::toDto)
                .collect(Collectors.toList());
        log.info("Pobrano wszystkie oferty");
        return result;
    }

    @Transactional
    public List<BidDto> getBidsByItemId(Long itemId) {
        log.info("Pobieranie ofert dla przedmiotu o id: {}", itemId);
        List<Bid> bids = bidRepository.getBidsByItemId(itemId);
        List<BidDto> result = bids.stream()
                .map(bidMapper::toDto)
                .collect(Collectors.toList());
        log.info("Pobrano oferty dla przedmiotu o id: {}", itemId);
        return result;
    }

    @Transactional
    public Optional<BidDto> getWinnerBidByItemId(Long itemId) {
        log.info("Pobieranie zwycięskiej oferty dla przedmiotu o id: {}", itemId);
        List<BidDto> bids = getBidsByItemId(itemId);
        if (bids.isEmpty()) {
            return Optional.empty();
        } else {
            Optional<BidDto> result = Optional.ofNullable(bids.getLast());
            log.info("Pobrano zwycięską ofertę dla przedmiotu o id: {}", itemId);
            return result;
        }
    }

    @Transactional
    public void deleteBid(Long id) {
        log.info("Usuwanie oferty o id: {}", id);
        bidRepository.deleteById(id);
        log.info("Oferta o id {} została pomyślnie usunięta", id);
    }

    public boolean isUserAuthorizedToUpdateBid(Long id) {
        log.info("Sprawdzanie, czy użytkownik jest upoważniony do aktualizacji oferty o id: {}", id);
        Optional<BidDto> bidDto = getBidById(id);
        if (bidDto.isEmpty()) {
            throw new IllegalArgumentException("Oferta o id " + id + " nie istnieje");
        }

        Long bidderId = bidDto.get().getBidderId();
        boolean isAuthorized = userService.isUserAuthorizedToUpdate(bidderId);
        log.info("Użytkownik {} jest {} do aktualizacji oferty o id: {}", bidderId, isAuthorized ? "upoważniony" : "nieupoważniony", id);
        return isAuthorized;
    }
}
