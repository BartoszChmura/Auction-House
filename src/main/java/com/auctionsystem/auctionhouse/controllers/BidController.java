package com.auctionsystem.auctionhouse.controllers;

import com.auctionsystem.auctionhouse.dtos.BidDto;
import com.auctionsystem.auctionhouse.services.BidService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bid")
@Slf4j
@Tag(name = "Bid", description = "Endpoints for bidding on items")
public class BidController {

    private final BidService bidService;

    @Autowired
    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping
    @Operation(summary = "Bid on an item", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> addBid(@RequestBody BidDto bidDto) {
        log.info("Received BidDto:" + bidDto.getBidAmount() + " " + bidDto.getItemId() + " " + bidDto.getBidderId() + " " + bidDto.getId());
        BidDto savedBid = bidService.saveBid(bidDto);
        return ResponseEntity.ok(savedBid);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a bid by id", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getBidById(@PathVariable Long id) {
        Optional<?> bidDto = bidService.getBidById(id);
        if (bidDto.isPresent()) {
            return ResponseEntity.ok(bidDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bid with id " + id + " does not exist");
        }
    }

    @GetMapping("/winner/{itemId}")
    @Hidden
    public ResponseEntity<?> getWinnerBidByItemId(@PathVariable Long itemId) {
        Optional<BidDto> winnerBid = bidService.getWinnerBidByItemId(itemId);
        if (winnerBid.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no winner for the item with id " + itemId);
        }
        return ResponseEntity.ok(winnerBid.get());
    }

    @GetMapping("/item/{itemId}")
    @Hidden
    public ResponseEntity<?> getBidsByItemId(@PathVariable Long itemId) {
        List<BidDto> bids = bidService.getBidsByItemId(itemId);
        return ResponseEntity.ok(bids);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all bids", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<BidDto>> getAllBids() {
        List<BidDto> bids = bidService.getAllBids();
        return ResponseEntity.ok(bids);
    }

    @DeleteMapping("/{id}")
    @Hidden
    public ResponseEntity<?> deleteBid(@PathVariable Long id) {
        Optional<BidDto> bidDto = bidService.getBidById(id);
        if (bidDto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bid with id " + id + " does not exist");
        }
        if (!bidService.isUserAuthorizedToUpdateBid(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to delete someone else's bid");
        }
        bidService.deleteBid(id);
        return ResponseEntity.ok("Bid with id " + id + " has been deleted");
    }
}
