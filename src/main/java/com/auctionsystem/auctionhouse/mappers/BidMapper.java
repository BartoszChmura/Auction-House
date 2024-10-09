package com.auctionsystem.auctionhouse.mappers;

import com.auctionsystem.auctionhouse.dtos.BidDto;
import com.auctionsystem.auctionhouse.entities.Bid;
import com.auctionsystem.auctionhouse.entities.Item;
import com.auctionsystem.auctionhouse.entities.User;
import org.springframework.stereotype.Component;

@Component
public class BidMapper {

    public BidDto toDto(Bid bid) {
        BidDto bidDto = new BidDto();
        bidDto.setId(bid.getId());
        bidDto.setItemId(bid.getItem().getId());
        bidDto.setBidderId(bid.getBidder().getId());
        bidDto.setBidAmount(bid.getBidAmount());
        bidDto.setBidTime(bid.getBidTime());

        return bidDto;
    }

    public Bid toEntity(BidDto bidDto) {
        Bid bid = new Bid();
        bid.setId(bidDto.getId());
        Item item = new Item();
        item.setId(bidDto.getItemId());
        bid.setItem(item);
        User bidder = new User();
        bidder.setId(bidDto.getBidderId());
        bid.setBidder(bidder);
        bid.setBidAmount(bidDto.getBidAmount());
        bid.setBidTime(bidDto.getBidTime());

        return bid;
    }
}