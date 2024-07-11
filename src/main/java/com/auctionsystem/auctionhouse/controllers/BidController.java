package com.auctionsystem.auctionhouse.controllers;

import com.auctionsystem.auctionhouse.dtos.BidDto;
import com.auctionsystem.auctionhouse.services.BidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bid")
@Slf4j
public class BidController {

    private final BidService bidService;

    @Autowired
    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping
    public ResponseEntity<?> addBid(@RequestBody BidDto bidDto) {
        log.info("Received BidDto:" + bidDto.getBidAmount() + " " + bidDto.getItemId() + " " + bidDto.getBidderId() + " " + bidDto.getId());
        BidDto savedBid = bidService.saveBid(bidDto);
        return ResponseEntity.ok(savedBid);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBidById(@PathVariable Long id) {
        Optional<?> bidDto = bidService.getBidById(id);
        if (bidDto.isPresent()) {
            return ResponseEntity.ok(bidDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Licytacja o id " + id + " nie istnieje");
        }
    }

    @GetMapping("/winner/{itemId}")
    public ResponseEntity<?> getWinnerBidByItemId(@PathVariable Long itemId) {
        Optional<BidDto> winnerBid = bidService.getWinnerBidByItemId(itemId);
        if (winnerBid.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie ma zwycięzcy na przedmiot o id " + itemId);
        }
        return ResponseEntity.ok(winnerBid.get());
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> getBidsByItemId(@PathVariable Long itemId) {
        List<BidDto> bids = bidService.getBidsByItemId(itemId);
        return ResponseEntity.ok(bids);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BidDto>> getAllBids() {
        List<BidDto> bids = bidService.getAllBids();
        return ResponseEntity.ok(bids);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBid(@PathVariable Long id) {
        Optional<BidDto> bidDto = bidService.getBidById(id);
        if (bidDto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Licytacja o id " + id + " nie istnieje");
        }
        if (!bidService.isUserAuthorizedToUpdateBid(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nie masz uprawnień do usunięcia czyjejś oferty");
        }
        bidService.deleteBid(id);
        return ResponseEntity.ok("Licytacja o id " + id + " została usunięta");
    }
}
